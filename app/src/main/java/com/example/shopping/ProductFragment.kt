package com.example.shopping

import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.model.CarouselImage
import com.example.shopping.model.SelectedProduct
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProductFragment : Fragment() {

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

        println("cart is ${cartViewModel.cartItems.value}")
        val product=productViewModel.selectedProduct.value
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
                    val selectedProduct=SelectedProduct(product.productId,product.title,product.thumbnail,product.priceAfterDiscount,1,product.priceAfterDiscount)
                    cartViewModel.addToCart(selectedProduct)
                }
                job.join()
            }
            Snackbar.make(it,"Added to the Cart",Snackbar.LENGTH_LONG)
                .show()
        }

        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
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