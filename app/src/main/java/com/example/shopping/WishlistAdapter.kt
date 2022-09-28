package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
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
    private lateinit var wishListListener: WishlistListener

    fun setData(list: List<FavoriteProduct>){
        this.list=list
    }

    fun setWishListListener(listener: WishlistListener){
        wishListListener=listener
    }

    /*fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }*/


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        private fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        val imageView: ShapeableImageView
        val productName: TextView
        val productPrice: TextView
        val oldProductPrice:TextView
        val discount: TextView
        val menu:ImageButton
        val addToCartBtn:Button

        init {
            imageView=view.findViewById<ShapeableImageView>(R.id.favorite_imageview)
            productName=view.findViewById<TextView>(R.id.favorite_productName)
            productPrice=view.findViewById<TextView>(R.id.favorite_offer_price)
            oldProductPrice=view.findViewById<TextView>(R.id.favorite_product_price)
            discount=view.findViewById<TextView>(R.id.favorite_product_discount)
            menu=view.findViewById(R.id.wishlist_item_menu)
            addToCartBtn=view.findViewById(R.id.favorite_add_to_cart_btn)

            addToCartBtn.setOnClickListener {
                wishListListener.addToCart(adapterPosition)
            }

            menu.setOnClickListener {
                val popupMenu=PopupMenu(menu.context,menu)
                popupMenu.inflate(R.menu.wishlist_menu)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.remove_action_wishlist->{
                            println("Clicked item: $adapterPosition")
                            wishListListener.removeItem(adapterPosition)
                            true
                        }
                        else->{
                            println(adapterPosition)
                            false
                        }
                    }
                }
                popupMenu.show()
            }



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
            discount.text="${product.discountPercentage}%"
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
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_wish_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}