package com.example.shopping.home.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.util.ItemClickListener
import com.example.shopping.R
import com.example.shopping.home.enums.Orientation
import com.google.android.material.imageview.ShapeableImageView

class AutoScrollableCarouselAdapter(private val list:List<Int>):RecyclerView.Adapter<AutoScrollableCarouselAdapter.ViewHolder>() {

    private lateinit var listener: ItemClickListener
    private var orientation= Orientation.PORTRAIT

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }

    fun setOrientation(landscape: Orientation) {
        this.orientation=landscape
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageView: ShapeableImageView =view.findViewById(R.id.slider_image)
        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_offer,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (orientation==Orientation.LANDSCAPE){
            holder.imageView.scaleType=ImageView.ScaleType.FIT_CENTER
        }
        holder.imageView.setImageResource(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}