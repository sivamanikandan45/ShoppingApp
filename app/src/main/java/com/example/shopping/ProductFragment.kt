package com.example.shopping

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.model.CarouselImage
import com.example.shopping.model.RecentlyViewed
import com.example.shopping.model.SelectedProduct
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.RecentlyViewedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProductFragment : Fragment() {

    private val recentlyViewedViewModel:RecentlyViewedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productViewModel:ProductViewModel by activityViewModels()
        val cartViewModel:CartViewModel by activityViewModels()

        /*cartViewModel.noOfItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val badgeDrawable=(activity as AppCompatActivity).supportActionBar.men
            badgeDrawable.isVisible=true
            //badgeDrawable.number= cartViewModel.noOfItem.value!!
            badgeDrawable.backgroundColor= Color.RED
            badgeDrawable.badgeTextColor= Color.WHITE
        })*/

        /*if(cartViewModel.noOfItem.value!=0){
            val badgeDrawable=bottomNavigationView.getOrCreateBadge(R.id.cart)
            badgeDrawable.isVisible=true
            //badgeDrawable.number= cartViewModel.noOfItem.value!!
            badgeDrawable.backgroundColor= Color.RED
            badgeDrawable.badgeTextColor= Color.WHITE
        }*/

        println("cart is ${cartViewModel.cartItems.value}")
        val product=productViewModel.selectedProduct.value
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


        val productNameTextView:TextView=view.findViewById(R.id.product_name)
        val brandTextView:TextView=view.findViewById(R.id.brand_name)
        val ratingBar:RatingBar=view.findViewById(R.id.product_rating_bar)
        val descriptionTextView:TextView=view.findViewById(R.id.product_description)
        val productPriceTextView:TextView=view.findViewById(R.id.product_cost)
        val ratedValueTextView:TextView=view.findViewById(R.id.rating_value)
        val oldPrice:TextView=view.findViewById(R.id.product_price)
        val discount:TextView=view.findViewById(R.id.product_discount)
        val similarProductRecyclerView=view.findViewById<RecyclerView>(R.id.similar_product_recyler)

        lifecycleScope.launch {
            val job=launch(Dispatchers.IO){
                val list= product?.category?.let { productViewModel.getCategoryFromDB(it) }
                withContext(Dispatchers.Main){
                    val adapter=SimilarProductListAdapter()
                    list?.remove(product)
                    adapter.setData(list!!)
                    /*adapter.setOnItemClickListener(object : ItemClickListener{
                        override fun onItemClick(position: Int) {
                            parentFragmentManager.commit {
                                addToBackStack(null)
                                val viewModel:ProductViewModel by activityViewModels()
                                viewModel.selectedProduct.value= list[position]
                                replace(R.id.category_fragment_container,ProductFragment() )
                            }
                        }
                    })*/
                    similarProductRecyclerView.adapter=adapter
                    val layoutManager=LinearLayoutManager(requireContext())
                    layoutManager.orientation=LinearLayoutManager.HORIZONTAL
                    similarProductRecyclerView.layoutManager=layoutManager
                }
            }
            job.join()
        }

        productNameTextView.text=product?.title
        productPriceTextView.text="$"+product?.priceAfterDiscount.toString()
        brandTextView.text=product?.brand
        ratingBar.rating= product?.rating?.toFloat()!!
        descriptionTextView.text=product.description
        ratedValueTextView.text=product.rating
        oldPrice.text="$"+product.originalPrice.toString()
        oldPrice.showStrikeThrough(true)
        discount.text=product.discountPercentage.toString()+"% Off"

        val addToCartBtn=view.findViewById<Button>(R.id.add_to_cart_button)
        addToCartBtn.setOnClickListener {
            GlobalScope.launch {
                val job=launch (Dispatchers.IO){
                    val selectedProduct=SelectedProduct(product.productId,product.title,product.brand,product.thumbnail,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,1,product.originalPrice,product.priceAfterDiscount)
                    cartViewModel.addToCart(selectedProduct)
                }
                job.join()
            }
            Snackbar.make(it,"Added to the Cart",Snackbar.LENGTH_LONG)
                .show()
        }

        //(activity as AppCompatActivity).supportActionBar?.title=product.title
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_fragment_menu,menu)
    }

    fun TextView.showStrikeThrough(show: Boolean) {
        paintFlags =
            if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}