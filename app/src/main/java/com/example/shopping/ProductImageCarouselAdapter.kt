package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class ProductImageCarouselAdapter(private val list:List<String>) : RecyclerView.Adapter<ProductImageCarouselAdapter.ViewHolder>(){

    private lateinit var listener: ItemClickListener

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
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
        var bitmapValue: Bitmap?=null
        GlobalScope.launch {
            val job=launch(Dispatchers.IO) {
                val imageUrl = URL(list[position])
                bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
            }
            job.join()
            val imageSettingCoroutine=launch(Dispatchers.Main){
                holder.imageView.setImageBitmap(bitmapValue)
            }
            imageSettingCoroutine.join()
        }
        holder.imageView.setImageBitmap(bitmapValue)
        //Picasso.get().load(list[position]).into(holder.imageView);
    }

    override fun getItemCount(): Int {
        return list.size
    }
}