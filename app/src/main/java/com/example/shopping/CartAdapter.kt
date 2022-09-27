package com.example.shopping

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.SelectedProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat

class CartAdapter(val context: Context):RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private lateinit var list: List<SelectedProduct>
    private lateinit var listener: QuantityButtonListener
    //private val cartViewModel= ViewModelProvider(requireContext()).get(CartViewModel::class.java)

    fun setData(list: List<SelectedProduct>){
        this.list=list
    }

    fun setOnQuantityClickListener(listener: QuantityButtonListener){
        this.listener=listener
    }

    inner class ViewHolder(view:View,listener: QuantityButtonListener):RecyclerView.ViewHolder(view){


        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        fun bind(selectedProduct: SelectedProduct) {
            productNameTextView.text=selectedProduct.productName

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            val caad= df.format(selectedProduct.priceForSelectedQuantity).toDouble()

            productPriceTextView.text="$"+caad.toString()
            productBrandTextView.text=selectedProduct.productBrand
            productOldPriceTextView.text="$"+selectedProduct.olcPriceForSelectedQuantity.toString()
            productOldPriceTextView.showStrikeThrough(true)
            discountTextView.text=selectedProduct.discount.toString()+"% OFF"
            quantityTextView.text=selectedProduct.quantity.toString()

            increaseButton.setOnClickListener {
                var quantity=quantityTextView.text.toString().toInt()
                if(quantity<10){
                    quantity++
                    quantityTextView.text=quantity.toString()
                    listener.onIncreaseClicked(adapterPosition)
                }else{
                    Toast.makeText(it.context,"You can add only 10 items from a particular Product", Toast.LENGTH_SHORT).show()
                }
            }

            decreaseButton.setOnClickListener{
                var quantity=quantityTextView.text.toString().toInt()
                if(quantity>1){
                    quantity--
                    quantityTextView.text=quantity.toString()
                    listener.onDecreaseClicked(adapterPosition)
                }else if(quantity==1){
                    Toast.makeText(it.context,"Swipe Item to remove from the Cart", Toast.LENGTH_SHORT).show()
                }
            }

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
        val increaseButton:ImageButton
        val decreaseButton:ImageButton
        val quantityTextView:TextView

        init {
            productImageView=view.findViewById(R.id.cart_item_product_image)
            productNameTextView=view.findViewById(R.id.cart_item_product_name)
            productPriceTextView=view.findViewById(R.id.cart_item_product_price)
            productBrandTextView=view.findViewById(R.id.cart_item_product_brand)
            productOldPriceTextView=view.findViewById(R.id.cart_item_product_old_price)
            discountTextView=view.findViewById(R.id.cart_item_offer)
            increaseButton=view.findViewById(R.id.increase_qty_btn)
            decreaseButton=view.findViewById(R.id.decrease_qty_btn)
            quantityTextView=view.findViewById(R.id.cart_qty)

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_cart,parent,false)
        return ViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}