package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.OrderedProduct
import com.example.shopping.model.OrderedProductEntity
import com.example.shopping.util.ProductImageMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat

class OrderedProductListAdapter :RecyclerView.Adapter<OrderedProductListAdapter.ViewHolder>(){
    private var list= listOf<OrderedProduct>()
    private lateinit var listener:ItemClickListener

    fun setData(list: List<OrderedProduct>){
        this.list=list
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        println("Called and set")
        this.listener=listener
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        fun bind(orderedProductEntity: OrderedProduct) {
            productNameTextView.text=orderedProductEntity.productName
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            val caad= df.format(orderedProductEntity.priceForSelectedQuantity).toDouble()
            productPriceTextView.text="₹"+caad.toString()
            productBrandTextView.text=orderedProductEntity.brand
            productOldPriceTextView.text="₹"+orderedProductEntity.oldPriceForSelectedQuantity.toString()
            productOldPriceTextView.showStrikeThrough(true)
            discountTextView.text=orderedProductEntity.discount.toString()+"% OFF"
            quantityTextView.text=orderedProductEntity.quantity.toString()

            var bitmapValue: Bitmap?=null

            val bitmap: Bitmap? = ProductImageMemoryCache.getBitmapFromMemCache(orderedProductEntity.productId.toString())?.also {
                println("Fetched from cache at $adapterPosition")
                productImageView.setImageBitmap(it)
                //progressBar.visibility=View.GONE
            } ?:run{
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        val imageUrl = URL(orderedProductEntity.thumbnail)
                        withContext(Dispatchers.Main) {
                            productImageView.setImageResource(R.drawable.placeholder)
                        }
                        try{
                            bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                        }catch (exception: IOException){
                            println("Exception caught")
                        }
                        //productImageView.setImageResource(R.drawable.placeholder)
                        //bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                    }
                    job.join()
                    val imageSettingCoroutine=launch(Dispatchers.Main){
                        if(bitmapValue!=null){
                            productImageView.setImageBitmap(bitmapValue)
                            ProductImageMemoryCache.addBitmapToCache(orderedProductEntity.productId.toString(),bitmapValue!!)
                        }
                    }
                    imageSettingCoroutine.join()
                }
                null
            }

//            productImageView.setImageBitmap(bitmapValue)
        }

        val productImageView: ImageView
        val productNameTextView: TextView
        val productPriceTextView: TextView
        val productBrandTextView: TextView
        val productOldPriceTextView: TextView
        val discountTextView: TextView
        val quantityTextView: TextView

        init {
            productImageView=view.findViewById(R.id.cart_item_product_image)
            productNameTextView=view.findViewById(R.id.cart_item_product_name)
            productPriceTextView=view.findViewById(R.id.cart_item_product_price)
            productBrandTextView=view.findViewById(R.id.cart_item_product_brand)
            productOldPriceTextView=view.findViewById(R.id.cart_item_product_old_price)
            discountTextView=view.findViewById(R.id.cart_item_offer)
            quantityTextView=view.findViewById(R.id.cart_qty)

            view.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_selected_product,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}