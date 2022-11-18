package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.util.ProductImageMemoryCache
import com.example.shopping.util.WishlistDiffUtil
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class WishlistAdapter:RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {
    //private lateinit var list: List<FavoriteProduct>
    private var list= listOf<FavoriteProduct>()
    private lateinit var wishListListener: WishlistListener
    private lateinit var listener:ItemClickListener

    fun setData(list: List<FavoriteProduct>){
        println("set Data is called")
        val oldList=this.list
        println("old list is $oldList")
        println("New list is $list")
        val wishlistDiffUtil= WishlistDiffUtil(oldList,list)
        val diffResult=DiffUtil.calculateDiff(wishlistDiffUtil)
        this.list=list
        diffResult.dispatchUpdatesTo(this)
    }

    fun setWishListListener(listener: WishlistListener){
        wishListListener=listener
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }


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
        val menuParent:ConstraintLayout
        val addToCartBtn:Button
        var loadingPosition=-1
        //val progressBar:ProgressBar

        init {
            imageView=view.findViewById<ShapeableImageView>(R.id.favorite_imageview)
            productName=view.findViewById<TextView>(R.id.favorite_productName)
            productPrice=view.findViewById<TextView>(R.id.favorite_offer_price)
            oldProductPrice=view.findViewById<TextView>(R.id.favorite_product_price)
            discount=view.findViewById<TextView>(R.id.favorite_product_discount)
            menu=view.findViewById(R.id.wishlist_item_menu)
            menuParent=view.findViewById(R.id.wishlist_item_parent)
            addToCartBtn=view.findViewById(R.id.favorite_add_to_cart_btn)
            //progressBar=view.findViewById(R.id.progress)

            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            addToCartBtn.setOnClickListener {
                wishListListener.addToCart(adapterPosition)
            }

            menu.setOnClickListener {
                displayPopUpMenu()
            }

            menuParent.setOnClickListener {
                displayPopUpMenu()
            }



            /*view.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }*/
        }



        fun bind(product: FavoriteProduct) {
            productName.text=product.title
            productPrice.text="₹"+product.priceAfterDiscount.toString()
            //productRatingBar.rating=product.rating.toFloat()
            oldProductPrice.text="₹"+product.originalPrice.toString()
            oldProductPrice.showStrikeThrough(true)
            discount.text="${product.discountPercentage}%"
            var bitmapValue: Bitmap?=null
            val bitmap: Bitmap? = ProductImageMemoryCache.getBitmapFromMemCache(product.productId.toString())?.also {
                println("Fetched from cache at $adapterPosition")
                imageView.setImageBitmap(it)
                //progressBar.visibility=View.GONE
            } ?:run{
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
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
                    job.join()
                }
                null
            }

            //imageView.setImageBitmap(bitmapValue)
            //progressBar.visibility=View.VISIBLE
        }

        private fun displayPopUpMenu() {
            val popupMenu = PopupMenu(menu.context, menu)
            popupMenu.inflate(R.menu.wishlist_menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.remove_action_wishlist -> {
                        println("Clicked item: $adapterPosition")
                        wishListListener.removeItem(adapterPosition)
                        true
                    }
                    else -> {
                        println(adapterPosition)
                        false
                    }
                }
            }
            popupMenu.show()
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
        holder.loadingPosition=position
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}