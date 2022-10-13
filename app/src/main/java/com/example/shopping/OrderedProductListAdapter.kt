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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat

class OrderedProductListAdapter :RecyclerView.Adapter<OrderedProductListAdapter.ViewHolder>(){
    private var list= listOf<OrderedProduct>()
    fun setData(list: List<OrderedProduct>){
        this.list=list
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        fun bind(orderedProduct: OrderedProduct) {
            productNameTextView.text=orderedProduct.productName
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            val caad= df.format(orderedProduct.priceForSelectedQuantity).toDouble()
            productPriceTextView.text="$"+caad.toString()
            productBrandTextView.text=orderedProduct.brand
            productOldPriceTextView.text="$"+orderedProduct.oldPriceForSelectedQuantity.toString()
            productOldPriceTextView.showStrikeThrough(true)
            discountTextView.text=orderedProduct.discount.toString()+"% OFF"
            quantityTextView.text=orderedProduct.quantity.toString()

            var bitmapValue: Bitmap?=null
            GlobalScope.launch {
                val job=launch(Dispatchers.IO) {
                    val imageUrl = URL(orderedProduct.thumbnail)
                    bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                }
                job.join()
                val imageSettingCoroutine=launch(Dispatchers.Main){
                    productImageView.setImageBitmap(bitmapValue)
                }
                imageSettingCoroutine.join()
            }
            productImageView.setImageBitmap(bitmapValue)
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