package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class TopOfferListAdapter:RecyclerView.Adapter<TopOfferListAdapter.ViewHolder>() {
    //private lateinit var list:ArrayList<Product>
    private var list= ArrayList<Product>()
    private lateinit var listener: ItemClickListener
    private var viewType=0

    fun setViewType(viewType:Int){
        this.viewType=viewType
    }

    //private lateinit var favoriteButtonListener:FavoriteButtonListener

    fun setData(list:ArrayList<Product>){
        this.list=list
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }

    /*fun setFavoriteButtonListener(listener: FavoriteButtonListener){
        favoriteButtonListener=listener
    }*/


    inner class ViewHolder(view: View, listener: ItemClickListener):RecyclerView.ViewHolder(view){

        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        val imageView: ImageView
        val productNameTextView: TextView
        //        val productBrandTextView:TextView
        val productOldPriceTextView: TextView
        val productRatingBar: RatingBar
        val ratedValue: TextView
        val productNewPriceTextView: TextView
        val offer: TextView
        var loadingPosition=-1
        val progressBar:ProgressBar

        //val imageButton: ImageView

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
            progressBar=view.findViewById(R.id.progress)
            //imageButton=view.findViewById(R.id.favorite_button)
            //checkBox=view.findViewById(R.id.heart_checkbox)

            /*checkBox.setOnCheckedChangeListener { compoundButton, b ->
                favoriteButtonListener.handle(adapterPosition)
            }*/




            /*imageButton.setOnClickListener {
                favoriteButtonListener.handle(adapterPosition)
            }*/

            view.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

        fun bind(product: Product) {
            var bitmapValue: Bitmap?=null
            //var imageUrl:Uri?=null
            productNameTextView.text=product.title//.capitalize()//product.brand+" "+
            //Picasso.get().load(product.thumbnail).into(imageView);
            GlobalScope.launch {
                val job=launch(Dispatchers.IO) {
                    val imageUrl = URL(product.thumbnail)
                    bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                    withContext(Dispatchers.Main){
                        if(loadingPosition==adapterPosition){
                            if(list[adapterPosition].productId==product.productId){
                                imageView.setImageBitmap(bitmapValue)
                                progressBar.visibility=View.GONE
                            }
                        }
                    }
                    //bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                }
                job.join()
                /*val imageSettingCoroutine=launch(Dispatchers.Main){
                    imageView.setImageBitmap(bitmapValue)
                    //imageView.setImageURI(imageUrl)
                }
                imageSettingCoroutine.join()*/
            }

            /*if(product.favorite){
                imageButton.setImageResource(R.drawable.heart_red)
            }*/

            imageView.setImageBitmap(bitmapValue)
            progressBar.visibility=View.VISIBLE
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

        return if(viewType==0){
            val view=
                LayoutInflater.from(parent.context).inflate(R.layout.item_top_offer,parent,false)
            ViewHolder(view,listener)
        }else{
            val view=
                LayoutInflater.from(parent.context).inflate(R.layout.item_search,parent,false)
            ViewHolder(view,listener)
        }
        /*when(viewType){
            0->{
                val view=
                    LayoutInflater.from(parent.context).inflate(R.layout.item_top_offer,parent,false)
                return ViewHolder(view,listener)
            }
            1->{
                val view=
                    LayoutInflater.from(parent.context).inflate(R.layout.item_search,parent,false)
                return ViewHolder(view,listener)
            }
        }*/
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
        //holder.imageButton.setImageResource(R.drawable.border_heart)
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }
}