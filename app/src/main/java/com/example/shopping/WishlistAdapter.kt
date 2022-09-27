package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.FavoriteProduct
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class WishlistAdapter:RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {
    private lateinit var list: List<FavoriteProduct>
    //private lateinit var listener: ItemClickListener

    fun setData(list: List<FavoriteProduct>){
        this.list=list
    }

    /*fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }*/


    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        val imageView: ShapeableImageView
        val productName: TextView
        val productPrice: TextView
        val oldProductPrice:TextView
        //val productRatingBar: RatingBar
        val discount: TextView
        init {
            imageView=view.findViewById<ShapeableImageView>(R.id.favorite_imageview)
            productName=view.findViewById<TextView>(R.id.favorite_productName)
            productPrice=view.findViewById<TextView>(R.id.favorite_offer_price)
            oldProductPrice=view.findViewById<TextView>(R.id.favorite_product_price)
            discount=view.findViewById<TextView>(R.id.favorite_product_discount)
            /*view.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }*/
        }



        fun bind(product: FavoriteProduct) {
            productName.text=product.title
            productPrice.text="$"+product.priceAfterDiscount.toString()
            //productRatingBar.rating=product.rating.toFloat()
            oldProductPrice.text="$"+product.originalPrice.toString()
            oldProductPrice.showStrikeThrough(true)
            discount.text=product.rating
            var bitmapValue: Bitmap?=null
            GlobalScope.launch {
                val job=launch(Dispatchers.IO) {
                    val imageUrl = URL(product.thumbnail)
                    bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                }
                job.join()
                val imageSettingCoroutine=launch(Dispatchers.Main){
                    imageView.setImageBitmap(bitmapValue)
                }
                imageSettingCoroutine.join()
            }
            imageView.setImageBitmap(bitmapValue)
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.item_wish_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}