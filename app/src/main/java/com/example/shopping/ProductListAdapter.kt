package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Product
import com.example.shopping.util.ProductDiffUtil
import com.example.shopping.util.ProductImageMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class ProductListAdapter:RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    //private lateinit var list:List<Product>
    var list= listOf<Product>()
    private lateinit var listener: ItemClickListener

    private lateinit var favoriteButtonListener:FavoriteButtonListener

    fun setData(list:List<Product>){
        val oldList=this.list
        val diffUtil= ProductDiffUtil(oldList,list)
        println("The old list is $oldList")
        println("The new list is $list")
        val diffResult= DiffUtil.calculateDiff(diffUtil,false)
        this.list=list
        diffResult.dispatchUpdatesTo(this)
    }

    fun setList(oldList:List<Product>,newList:List<Product>){
        val diffUtil= ProductDiffUtil(oldList,newList)
        println("The old list is $oldList")
        println("The new list is $newList")
        val diffResult= DiffUtil.calculateDiff(diffUtil,false)
        this.list=newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        println("Called and set")
        this.listener=listener
    }

    fun setFavoriteButtonListener(listener: FavoriteButtonListener){
        favoriteButtonListener=listener
    }


    inner class ViewHolder(view: View,listener: ItemClickListener):RecyclerView.ViewHolder(view){

        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        var loadingPosition:Int=-1
        val imageView:ImageView
        val productNameTextView:TextView
//        val productBrandTextView:TextView
        val productOldPriceTextView:TextView
        val productRatingBar:RatingBar
        val ratedValue:TextView
        val productNewPriceTextView:TextView
        val offer:TextView

        val imageButton:ImageView
        //val progressBar:ProgressBar

        //val checkBox:CheckBox


        init {
            imageView=view.findViewById(R.id.product_card_imageview)
            productNameTextView=view.findViewById(R.id.productCard_ProductName)
            productOldPriceTextView=view.findViewById(R.id.product_price)
            productOldPriceTextView.showStrikeThrough(true)
            //productBrandTextView=view.findViewById(R.id.productCard_brand)
            productRatingBar=view.findViewById(R.id.product_card_rating_bar)
            ratedValue=view.findViewById(R.id.product_card_rated_value)
            productNewPriceTextView=view.findViewById(R.id.product_card_offer_price)
            offer=view.findViewById(R.id.product_discount)
            imageButton=view.findViewById(R.id.favorite_button)
            //progressBar=view.findViewById(R.id.progress)
            //checkBox=view.findViewById(R.id.heart_checkbox)

            /*checkBox.setOnCheckedChangeListener { compoundButton, b ->
                favoriteButtonListener.handle(adapterPosition)
            }*/

            imageButton.setOnClickListener {
                val product=list[adapterPosition]
                if(product.favorite){
                    imageButton.setImageResource(R.drawable.border_heart)
                }else{
                    imageButton.setImageResource(R.drawable.heart_red)
                }

                GlobalScope.launch {
                    val job=launch {
                        favoriteButtonListener.handle(adapterPosition)
                    }
                    job.join()
                    list[adapterPosition].favorite=!list[adapterPosition].favorite
                }
            }

            view.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

        fun bind(product: Product) {
            var bitmapValue:Bitmap?=null
            imageView.setImageBitmap(bitmapValue)
            //progressBar.visibility=View.VISIBLE
            //var imageUrl:Uri?=null
            productNameTextView.text=product.title//.capitalize()//product.brand+" "+
            //Picasso.get().load(product.thumbnail).into(imageView);
            val bitmap: Bitmap? = ProductImageMemoryCache.getBitmapFromMemCache(product.productId.toString())?.also {
                println("Fetched from cache at $adapterPosition")
                imageView.setImageBitmap(it)
                //progressBar.visibility=View.GONE
            } ?:run{
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        val imageUrl = URL(product.thumbnail)
                        imageView.setImageResource(R.drawable.placeholder)
                        bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                        withContext(Dispatchers.Main){
                            if(loadingPosition==adapterPosition){
                                if(list[adapterPosition].productId==product.productId){
                                    imageView.setImageBitmap(bitmapValue)
                                    ProductImageMemoryCache.addBitmapToCache(product.productId.toString(),bitmapValue!!)
                                    //progressBar.visibility=View.GONE
                                }
                            }
                        }
                    }
                    job.join()
                }
                null
            }


            if(product.favorite){
                imageButton.setImageResource(R.drawable.heart_red)
            }

            //imageView.setImageBitmap(bitmapValue)
            productOldPriceTextView.text="₹"+product.originalPrice.toString()
            //productBrandTextView.text=product.brand
            productRatingBar.rating=product.rating.toFloat()
            ratedValue.text=product.rating
            productNewPriceTextView.text="₹"+product.priceAfterDiscount.toString()
            offer.text=product.discountPercentage.toString()+"% Off"
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_product_list,parent,false)
        return ViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loadingPosition=position
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.imageView.setImageBitmap(null)
        holder.imageButton.setImageResource(R.drawable.border_heart)
    }

}