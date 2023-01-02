package com.example.shopping.product.ui.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.util.ItemClickListener
import com.example.shopping.R
import com.example.shopping.product.model.CarouselImage
import com.example.shopping.product.ui.util.CarouselImageMemoryCache
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.*
import java.io.IOException
import java.net.URL

class ProductImageCarouselAdapter(private val list:List<CarouselImage>) : RecyclerView.Adapter<ProductImageCarouselAdapter.ViewHolder>(){

    private lateinit var listener: ItemClickListener

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(image:CarouselImage) {
            var bitmapValue: Bitmap?=null
            val bitmap: Bitmap? = CarouselImageMemoryCache.getBitmapFromMemCache(image.imageId.toString())?.also {
                println("Fetched from cache at  carousel cache$adapterPosition")
                imageView.setImageBitmap(it)
            } ?:run{
                CoroutineScope(Dispatchers.IO).launch {
                    val job=launch(Dispatchers.IO) {
                        val imageUrl = URL(image.imageUrl)
                        withContext(Dispatchers.Main){
                            imageView.setImageBitmap(bitmapValue)
                            //imageView.setImageResource(R.drawable.placeholder)
                        }
                        try{
                            bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                        }catch (exception: IOException){
                            println("Exception caught")
                        }
                        //bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                        withContext(Dispatchers.Main){
                            if(bitmapValue!=null){
                                imageView.setImageBitmap(bitmapValue)
                                CarouselImageMemoryCache.addBitmapToCache(image.imageId.toString(),bitmapValue!!)
                            }
                        }
                    }
                    job.join()
                }
                null
            }
        }

        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
        val imageView: ShapeableImageView =view.findViewById(R.id.slider_image)
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_auto_scrollable_carousel,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.imageView.setImageResource(list[position])
        holder.bind(list[position])
        //holder.imageView.setImageBitmap(bitmapValue)
        //Picasso.get().load(list[position]).into(holder.imageView);
    }

    override fun getItemCount(): Int {
        return list.size
    }
}