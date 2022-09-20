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
import com.example.shopping.model.SelectedProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class CartAdapter:RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private lateinit var list: List<SelectedProduct>

    fun setData(list: List<SelectedProduct>){
        this.list=list
    }

    class ViewHolder(view:View):RecyclerView.ViewHolder(view){

        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        fun bind(selectedProduct: SelectedProduct) {
            productNameTextView.text=selectedProduct.productName
            productPriceTextView.text="$"+selectedProduct.pricePerProduct.toString()
            productBrandTextView.text=selectedProduct.productBrand
            productOldPriceTextView.text="$"+selectedProduct.oldPricePerProduct.toString()
            productOldPriceTextView.showStrikeThrough(true)
            discountTextView.text=selectedProduct.discount.toString()+"% OFF"

            var bitmapValue: Bitmap?=null
            GlobalScope.launch {
                val job=launch(Dispatchers.IO) {
                    val imageUrl = URL(selectedProduct.imageUrl)
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

        val productImageView:ImageView
        val productNameTextView:TextView
        val productPriceTextView:TextView
        val productBrandTextView:TextView
        val productOldPriceTextView:TextView
        val discountTextView:TextView
        init {
            productImageView=view.findViewById(R.id.cart_item_product_image)
            productNameTextView=view.findViewById(R.id.cart_item_product_name)
            productPriceTextView=view.findViewById(R.id.cart_item_product_price)
            productBrandTextView=view.findViewById(R.id.cart_item_product_brand)
            productOldPriceTextView=view.findViewById(R.id.cart_item_product_old_price)
            discountTextView=view.findViewById(R.id.cart_item_offer)

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_cart,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}