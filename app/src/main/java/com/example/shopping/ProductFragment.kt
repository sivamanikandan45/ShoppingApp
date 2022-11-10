package com.example.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.model.*
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.FavoriteViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.RecentlyViewedViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProductFragment : Fragment() {

    private val recentlyViewedViewModel:RecentlyViewedViewModel by activityViewModels()
    private val viewModel:ProductViewModel by activityViewModels()
    private val favoriteViewModel:FavoriteViewModel by activityViewModels()
    val cartViewModel:CartViewModel by activityViewModels()
    private lateinit var cartItem: MenuItem
    //private lateinit var toolbar: Toolbar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //toolbar=activity.findViewById(R.id.toolbar)
        //setHasOptionsMenu(true) --->Edited
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        cartItem=menu.findItem(R.id.cart_menu)
        /*val badge = BadgeDrawable.create(requireContext())
        BadgeUtils.attachBadgeDrawable(badge, view,menu )*/
        /*val badgeDrawable=cartItem.getOrCreateBadge(R.id.cart)
        badgeDrawable.isVisible=true
        badgeDrawable.number=it
        badgeDrawable.backgroundColor=Color.parseColor("#b00020")
        badgeDrawable.badgeTextColor=Color.WHITE*/

        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onStart() {
        var amount=0
        GlobalScope.launch {
            val job=launch{
                amount=cartViewModel.getCartItemCount()
            }
            job.join()
            println("Cart count is $amount")
        }
        super.onStart()
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productViewModel:ProductViewModel by activityViewModels()

        (activity as AppCompatActivity)?.supportActionBar?.hide()
        val toolbar=view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.product_fragment_menu)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener { view ->
            activity?.onBackPressed()
        }



        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.cart_menu->{
                    val intent= Intent(requireContext(),MainActivity::class.java)
                    intent.putExtra("fragment","cart")
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK+Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    true
                }
                else -> {
                    false
                }
            }
        }

        val buyNowButton=view.findViewById<Button>(R.id.buy_now_button)
        val quantityTextView:TextView=view.findViewById(R.id.cart_qty)



        cartViewModel.noOfItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it==0){
                val badgeDrawable=BadgeDrawable.create(requireContext())
                badgeDrawable.isVisible=false
            }else{
                val badge = BadgeDrawable.create(requireContext())
                //badge.maxCharacterCount=3
                //badge.badgeGravity=BadgeDrawable.TOP_START
                badge.number= cartViewModel.noOfItem.value?:0
                BadgeUtils.attachBadgeDrawable(badge, toolbar, R.id.cart_menu )
            }
        })



        println("cart is ${cartViewModel.cartItems.value}")
        val product=productViewModel.selectedProduct.value
        println("Selected Product is $product")

        buyNowButton.setOnClickListener {
            val intent=Intent(requireContext(),CheckoutActivity::class.java)
            intent.putExtra("checkoutMode","buy_now")
            intent.putExtra("productId",product?.productId)
            intent.putExtra("quantity",quantityTextView.text.toString().toInt())
            startActivity(intent)
        }

        if(product!=null){
            val recentlyViewed=RecentlyViewed(product.productId,product.title,product.description,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,product.rating,product.stock,product.brand,product.category,product.thumbnail)
            GlobalScope.launch {
                val job = launch(Dispatchers.IO) { recentlyViewedViewModel.addToRecentlyViewed(recentlyViewed) }
                job.join()
            }

        }
        println("Selected product is $product")

        var list: MutableList<String> = mutableListOf<String>()
        GlobalScope.launch {
            val job=launch (Dispatchers.IO){
                list= product?.productId?.let { productViewModel.getImageUrlList(it) }!!
                println("got imaglist $list")
            }
            job.join()
            withContext(Dispatchers.Main){
                val autoScrollableCarousel=view.findViewById<ViewPager2>(R.id.product_image_carousel)
                //val images= listOf(R.drawable.image1,R.drawable.image2,R.drawable.image3)
                val autoScrollableCarouselAdapter=ProductImageCarouselAdapter(list)
                autoScrollableCarouselAdapter.setOnItemClickListener(object :ItemClickListener{
                    override fun onItemClick(position: Int) {
                        val intent=Intent(requireContext(),ProductImageActivity::class.java)
                        intent.putExtra("productId",product?.productId)
                        intent.putExtra("currentPosition",position)
                        startActivity(intent)
                    }

                })
                println("value set")
                autoScrollableCarousel.adapter = autoScrollableCarouselAdapter
                val timerTask: TimerTask = object : TimerTask() {
                    override fun run() {
                        autoScrollableCarousel.post {
                            autoScrollableCarousel.currentItem = (autoScrollableCarousel.currentItem + 1) % list.size
                        }
                    }
                }
                val timer = Timer()
                timer.schedule(timerTask, 1000, 3000)
            }
        }

        val favoriteButton:ImageView=view.findViewById(R.id.favorite_button)
        if(product?.favorite==true){
            favoriteButton.setImageResource(R.drawable.heart_red)
        }else{
            favoriteButton.setImageResource(R.drawable.border_heart)
        }

        favoriteButton.setOnClickListener{
            println("Favorite btn is clicked")
            if(product?.favorite!=true){
                favoriteButton.setImageResource(R.drawable.heart_red)
                addToFavorite(product)
                product?.favorite=true
                Snackbar.make(view,"Added to WishList",Snackbar.LENGTH_LONG)
                    .show()

            }else{
                favoriteButton.setImageResource(R.drawable.border_heart)
                removeFromFavorite(product)
                product?.favorite=false
                Snackbar.make(view,"Removed from WishList",Snackbar.LENGTH_LONG)
                    .show()
            }
        }


        val productNameTextView:TextView=view.findViewById(R.id.product_name)
        val brandTextView:TextView=view.findViewById(R.id.brand_name)
        val ratingBar:RatingBar=view.findViewById(R.id.product_rating_bar)
        val descriptionTextView:TextView=view.findViewById(R.id.product_description)
        val productPriceTextView:TextView=view.findViewById(R.id.product_cost)
        val ratedValueTextView:TextView=view.findViewById(R.id.rating_value)
        val oldPrice:TextView=view.findViewById(R.id.product_price)
        val discount:TextView=view.findViewById(R.id.product_discount)
        val similarProductRecyclerView=view.findViewById<RecyclerView>(R.id.similar_product_recyler)

        val increaseButton: ImageButton=view.findViewById(R.id.increase_qty_btn)
        val increaseTouchRegion:ConstraintLayout=view.findViewById(R.id.increaseButtonLayout)
        val decreaseButton: ImageButton=view.findViewById(R.id.decrease_qty_btn)
        val decreaseTouchRegion:ConstraintLayout=view.findViewById(R.id.decreaseButtonLayout)

        //val quantityTextView:TextView=view.findViewById(R.id.cart_qty)
        increaseButton.setOnClickListener {
            increaseCount(quantityTextView, it)
        }

        increaseTouchRegion.setOnClickListener {
            increaseCount(quantityTextView,it)
        }

        decreaseButton.setOnClickListener{
            decreaseCount(quantityTextView, it)
        }

        decreaseTouchRegion.setOnClickListener {
            decreaseCount(quantityTextView, it)
        }

        lifecycleScope.launch {
            val job=launch(Dispatchers.IO){
                val list= product?.category?.let { productViewModel.getCategoryFromDB(it) }
                withContext(Dispatchers.Main){
                    val adapter=SimilarProductListAdapter()
                    list?.remove(product)
                    adapter.setData(list!!)
                    adapter.setOnItemClickListener(object : ItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent=Intent(context,CategoryActivity::class.java)
                            intent.putExtra("fragment_name","product")
                            intent.putExtra("selected_product_id",list[position].productId)
                            startActivity(intent)
                            /*parentFragmentManager.commit {
                                addToBackStack(null)
                                val viewModel:ProductViewModel by activityViewModels()
                                viewModel.selectedProduct.value= list[position]
                                //viewModel.push(list[position])


                                replace(R.id.category_fragment_container,ProductFragment() )
                                *//*val intent=Intent(requireContext(),CategoryActivity::class.java)
                                intent.putExtra("SelectedProduct",position)
                                intent.putExtra("category",list[position].category)
                                startActivity(intent)*//*
                            }*/
                        }
                    })
                    similarProductRecyclerView.adapter=adapter
                    val layoutManager=LinearLayoutManager(requireContext())
                    layoutManager.orientation=LinearLayoutManager.HORIZONTAL
                    similarProductRecyclerView.layoutManager=layoutManager
                }
            }
            job.join()
        }

        if(product!=null){
            productNameTextView.text=product.title
//            (activity as AppCompatActivity)?.supportActionBar?.title=product.title
            productPriceTextView.text="₹"+product.priceAfterDiscount.toString()
            brandTextView.text=product.brand
            ratingBar.rating= product.rating?.toFloat()!!
            descriptionTextView.text=product.description
            ratedValueTextView.text=product.rating
            oldPrice.text="₹"+product.originalPrice.toString()
            oldPrice.showStrikeThrough(true)
            discount.text=product.discountPercentage.toString()+"% Off"

            val addToCartBtn=view.findViewById<Button>(R.id.add_to_cart_button)
            addToCartBtn.setOnClickListener {
                GlobalScope.launch {
                    val job=launch (Dispatchers.IO){
                        val count=quantityTextView.text.toString().toInt()
                        val oldPriceForSelectedQty=count*product.originalPrice
                        val priceForSelectedQty=count*product.priceAfterDiscount
                        val selectedProduct=SelectedProduct(product.productId,product.title,product.brand,product.thumbnail,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,count,oldPriceForSelectedQty,priceForSelectedQty)
                        cartViewModel.addToCart(selectedProduct)
                    }
                    job.join()
                }
                //val coord=view.findViewById<CoordinatorLayout>(R.id.button_layout)
                Snackbar.make(it,"Added to the Cart",Snackbar.LENGTH_LONG)
                    .show()
            }
        }


        toolbar.title=product?.title
//        (activity as AppCompatActivity).supportActionBar?.title=product?.title
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun decreaseCount(quantityTextView: TextView, it: View) {
        var quantity = quantityTextView.text.toString().toInt()
        if (quantity > 1) {
            quantity--
            quantityTextView.text = quantity.toString()
            //listener.onDecreaseClicked(adapterPosition)
        } else if (quantity == 1) {
            Toast.makeText(it.context, "Atleast 1 item should be selected", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun increaseCount(quantityTextView: TextView, it: View) {
        var quantity = quantityTextView.text.toString().toInt()
        if (quantity < 10) {
            quantity++
            quantityTextView.text = quantity.toString()
            //listener.onIncreaseClicked(adapterPosition)
        } else {
            Toast.makeText(
                it.context,
                "You can add only 10 items from a particular Product",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeFromFavorite(product: Product?) {
        GlobalScope.launch {
            val job=launch(Dispatchers.IO) {
                favoriteViewModel.deleteFromFavorites(product?.productId)
                product?.productId?.let { viewModel.removeFavorite(it) }
            }
            job.join()
        }
    }

    private fun addToFavorite(product: Product?) {
        GlobalScope.launch {
            val job = launch(Dispatchers.IO) {
                if (product != null) {
                    val favoriteProduct = FavoriteProduct(
                        product.productId,
                        product.title,
                        product.description,
                        product.originalPrice,
                        product.discountPercentage,
                        product.priceAfterDiscount,
                        product.rating,
                        product.stock,
                        product.brand,
                        product.category,
                        product.thumbnail
                    )
                    favoriteViewModel.addToFavorites(favoriteProduct)
                    viewModel.markAsFavorite(product.productId)
                }
            }
            job.join()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cart_menu->{
                val intent= Intent(requireContext(),MainActivity::class.java)
                intent.putExtra("fragment","cart")
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK+Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun TextView.showStrikeThrough(show: Boolean) {
        paintFlags =
            if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}