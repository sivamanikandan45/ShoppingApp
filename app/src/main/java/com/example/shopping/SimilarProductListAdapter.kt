package com.example.shopping

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Product
import com.example.shopping.model.RecentlyViewed
import com.example.shopping.util.CheckInternet
import com.example.shopping.util.ProductImageMemoryCache
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class SimilarProductListAdapter(val context: Context):RecyclerView.Adapter<SimilarProductListAdapter.ViewHolder>() {
    private lateinit var list: List<Product>
    private lateinit var listener: ItemClickListener

    fun setData(list: List<Product>){
        this.list=list
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageView:ShapeableImageView
        val productName:TextView
        val productPrice:TextView
        val productRatingBar:RatingBar
        val productRatedValue:TextView
        var loadingPosition=-1
        //val progressBar:ProgressBar
        init {
            imageView=view.findViewById<ShapeableImageView>(R.id.similar_product_imageView)
            productName=view.findViewById<TextView>(R.id.similar_product_name)
            productPrice=view.findViewById<TextView>(R.id.similar_product_price)
            productRatingBar=view.findViewById<RatingBar>(R.id.similar_product_rating_bar)
            productRatedValue=view.findViewById<TextView>(R.id.similar_product_rated_value)
            //progressBar=view.findViewById(R.id.progress)
            view.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }



        fun bind(product: Product) {
            productName.text=product.title
            productPrice.text="₹"+product.priceAfterDiscount.toString()
            productRatingBar.rating=product.rating.toFloat()
            productRatedValue.text=product.rating
            var bitmapValue: Bitmap?=null

            val bitmap: Bitmap? = ProductImageMemoryCache.getBitmapFromMemCache(product.productId.toString())?.also {
                println("Fetched from cache at $adapterPosition")
                imageView.setImageBitmap(it)
            }?:run{
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        println(Thread.currentThread().name)
                        if(CheckInternet.isNetwork(context)||CheckInternet.isConnectedNetwork(context)){
                            println("Network is available")
                            val imageUrl = URL(product.thumbnail)
                            withContext(Dispatchers.Main) {
                                imageView.setImageResource(R.drawable.placeholder)
                            }

                            //imageView.setImageResource(R.drawable.placeholder)
                            bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                            withContext(Dispatchers.Main){
                                if(loadingPosition==adapterPosition){
                                    if(list[adapterPosition].productId==product.productId){
                                        imageView.setImageBitmap(bitmapValue)
                                        ProductImageMemoryCache.addBitmapToCache(product.productId.toString(),bitmapValue!!)
                                    }
                                }
                            }
                        }
                    }
                    job.join()
                }
                null
            }
            //progressBar.visibility=View.VISIBLE
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_similar_product,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loadingPosition=position
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}