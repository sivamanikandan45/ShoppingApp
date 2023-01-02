package com.example.shopping.cart.ui.adapter

import android.graphics.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.util.ItemClickListener
import com.example.shopping.cart.ui.QuantityButtonListener
import com.example.shopping.R
import com.example.shopping.cart.model.SelectedProduct
import com.example.shopping.cart.ui.util.CartDiffUtil
import com.example.shopping.product.ui.util.ProductImageMemoryCache
import kotlinx.coroutines.*
import java.io.IOException
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat


class CartAdapter:RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    //var bitmapValue:Bitmap?=null
    //private lateinit var list: List<SelectedProductEntity>
    private var list= listOf<SelectedProduct>()
    private lateinit var quantityButtonListener: QuantityButtonListener
    private lateinit var itemClickListener: ItemClickListener
    //private val cartViewModel= ViewModelProvider(requireContext()).get(CartViewModel::class.java)

    fun setData(list: List<SelectedProduct>){
        //this.list=list
        val oldList=this.list
        val diffUtil= CartDiffUtil(oldList,list)
        val diffResult=DiffUtil.calculateDiff(diffUtil)
        this.list=list
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener=itemClickListener
    }



    private fun mergeMultiple(parts: Array<Bitmap>): Bitmap? {
        val result =
            Bitmap.createBitmap(parts[0].width * 2, parts[0].height * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        for (i in parts.indices) {
            canvas.drawBitmap(parts[i], parts[i].width * (i % 2).toFloat(), parts[i].height * (i / 2).toFloat(), paint)
        }
        return result
    }

    private fun mergeThemAll(orderImagesList: List<Bitmap>?): Bitmap? {
        var result: Bitmap? = null
        if (orderImagesList != null && orderImagesList.isNotEmpty()) {
            // if two images > increase the width only
            result = if (orderImagesList.size == 2) Bitmap.createBitmap(
                orderImagesList[0].width * 2,
                orderImagesList[0].height,
                Bitmap.Config.ARGB_8888
            ) else if (orderImagesList.size > 2) Bitmap.createBitmap(
                orderImagesList[0].width * 2, orderImagesList[0].height * 2, Bitmap.Config.ARGB_8888
            ) else  // don't increase anything
                Bitmap.createBitmap(
                    orderImagesList[0].width,
                    orderImagesList[0].height,
                    Bitmap.Config.ARGB_8888
                )
            val canvas = Canvas(result)
            val paint = Paint()
            for (i in orderImagesList.indices) {
                canvas.drawBitmap(
                    orderImagesList[i],
                    orderImagesList[i].width * (i % 2).toFloat(),
                    orderImagesList[i].height * (i / 2).toFloat(),
                    paint
                )
            }
        } else {
            Log.e("MergeError", "Couldn't merge bitmaps")
        }
        return result
    }

    fun setOnQuantityClickListener(listener: QuantityButtonListener){
        this.quantityButtonListener=listener
    }

    inner class ViewHolder(view:View,listener: QuantityButtonListener):RecyclerView.ViewHolder(view){

        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        fun bind(selectedProductEntity: SelectedProduct) {
            productNameTextView.text=selectedProductEntity.productName

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            val caad= df.format(selectedProductEntity.priceForSelectedQuantity).toDouble()

            productPriceTextView.text="₹"+caad.toString()
            productBrandTextView.text=selectedProductEntity.productBrand
            productOldPriceTextView.text="₹"+selectedProductEntity.oldPriceForSelectedQuantity.toString()
            productOldPriceTextView.showStrikeThrough(true)
            discountTextView.text=selectedProductEntity.discount.toString()+"% OFF"
            quantityTextView.text=selectedProductEntity.quantity.toString()

            increaseButton.setOnClickListener {
                increaseQuantity(it)
            }

            increaseTouchTarget.setOnClickListener {
                increaseQuantity(it)
            }

            decreaseButton.setOnClickListener{
                quantityButtonListener.onDecreaseClicked(adapterPosition)
                decreaseQuantity(it)
            }

            decreaseTouchTarget.setOnClickListener {
                quantityButtonListener.onDecreaseClicked(adapterPosition)
                decreaseQuantity(it)
            }

            var bitmapValue: Bitmap?=null
            val bitmap: Bitmap? = ProductImageMemoryCache.getBitmapFromMemCache(selectedProductEntity.productId.toString())?.also {
                println("Fetched from cache at $adapterPosition")
                productImageView.setImageBitmap(it)
            } ?:run{
                CoroutineScope(Dispatchers.IO).launch {
                    val job=launch(Dispatchers.IO) {
                        val imageUrl = URL(selectedProductEntity.imageUrl)
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
                        withContext(Dispatchers.Main){
                            if(loadingPosition==adapterPosition){
                                if(list[adapterPosition].productId==selectedProductEntity.productId){
                                    /*val bitMapArray= listOf(bitmapValue!!,bitmapValue!!,bitmapValue!!)
                                    val res=mergeThemAll(bitMapArray)*/
                                    if(bitmapValue!=null){
                                        productImageView.setImageBitmap(bitmapValue)
                                        ProductImageMemoryCache.addBitmapToCache(selectedProductEntity.productId.toString(),bitmapValue!!)
                                    }
                                }
                            }
                        }
                    }
                    job.join()
                }
                null
            }
        }

        private fun decreaseQuantity(it: View) {
            var quantity = quantityTextView.text.toString().toInt()
            //listener.onDecreaseClicked(adapterPosition)
            if (quantity > 1) {
                quantity--
                quantityTextView.text = quantity.toString()
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.UP
                val priceForSelectedQty= df.format(quantity*list[adapterPosition].pricePerProduct).toDouble()
                productPriceTextView.text="₹$priceForSelectedQty"
                productOldPriceTextView.text="₹${quantity*list[adapterPosition].oldPricePerProduct}"
                println("Quantity of $adapterPosition is updated as $quantity")
                println("The new price of $adapterPosition is updated as $productPriceTextView")
                println("The old price of $adapterPosition is updated as $productOldPriceTextView")
                //listener.onDecreaseClicked(adapterPosition)
                list[adapterPosition].apply {
                    this.quantity=quantity
                    priceForSelectedQuantity=quantity*list[adapterPosition].pricePerProduct
                    oldPriceForSelectedQuantity=quantity*list[adapterPosition].oldPricePerProduct
                }
                quantityButtonListener.updateQuantity(adapterPosition)
            } else if (quantity == 1) {
                /*Toast.makeText(it.context, "Swipe Item to remove from the Cart", Toast.LENGTH_SHORT)
                    .show()*/
                /*AlertDialog.Builder(it.context)
                    .setTitle("Remove Item")
                    .setMessage("Are you sure you want to remove this item?")
                    .setPositiveButton("REMOVE"){ _,_ ->
                        println("Hello world")
                    }
                    .setNegativeButton("CANCEL"){_,_ ->
                        //adapter.notifyItemChanged(position)
                    }
                    .show()*/
            }
        }

        private fun increaseQuantity(it: View) {
            var quantity = quantityTextView.text.toString().toInt()
            if (quantity < 10) {
                quantity++
                quantityTextView.text = quantity.toString()
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.UP
                val priceForSelectedQty= df.format(quantity*list[adapterPosition].pricePerProduct).toDouble()
                productPriceTextView.text="₹$priceForSelectedQty"
                productOldPriceTextView.text="₹${quantity*list[adapterPosition].oldPricePerProduct}"
                /*productPriceTextView.text=(quantity*list[adapterPosition].pricePerProduct).toString()
                productOldPriceTextView.text=(quantity*list[adapterPosition].oldPricePerProduct).toString()*/
                println("\n The value form the list is proce per product${list[adapterPosition].pricePerProduct} and oldprice per product ${list[adapterPosition].oldPricePerProduct}")
                println("Quantity of $adapterPosition is updated as $quantity")
                println("The new price of $adapterPosition is updated as ${(quantity*list[adapterPosition].pricePerProduct)}")
                println("The old price3 of $adapterPosition is updated as ${(quantity*list[adapterPosition].oldPricePerProduct)}")
                //listener.onIncreaseClicked(adapterPosition)
                list[adapterPosition].apply {
                    this.quantity=quantity
                    priceForSelectedQuantity=quantity*list[adapterPosition].pricePerProduct
                    oldPriceForSelectedQuantity=quantity*list[adapterPosition].oldPricePerProduct
                }
                quantityButtonListener.onIncreaseClicked(adapterPosition)
            } else {
                Toast.makeText(
                    it.context,
                    "You can add only 10 items from a particular Product",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val productImageView:ImageView
        val productNameTextView:TextView
        val productPriceTextView:TextView
        val productBrandTextView:TextView
        val productOldPriceTextView:TextView
        val discountTextView:TextView
        val increaseButton:ImageButton
        val increaseTouchTarget:ConstraintLayout
        val decreaseButton:ImageButton
        val decreaseTouchTarget:ConstraintLayout
        val quantityTextView:TextView
        var loadingPosition=-1

        init {
            productImageView=view.findViewById(R.id.cart_item_product_image)
            productNameTextView=view.findViewById(R.id.cart_item_product_name)
            productPriceTextView=view.findViewById(R.id.cart_item_product_price)
            productBrandTextView=view.findViewById(R.id.cart_item_product_brand)
            productOldPriceTextView=view.findViewById(R.id.cart_item_product_old_price)
            discountTextView=view.findViewById(R.id.cart_item_offer)
            increaseButton=view.findViewById(R.id.increase_qty_btn)
            increaseTouchTarget=view.findViewById(R.id.increaseButtonLayout)
            decreaseButton=view.findViewById(R.id.decrease_qty_btn)
            decreaseTouchTarget=view.findViewById(R.id.decreaseButtonLayout)
            quantityTextView=view.findViewById(R.id.cart_qty)

            view.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_cart,parent,false)
        return ViewHolder(view,quantityButtonListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loadingPosition=position
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}