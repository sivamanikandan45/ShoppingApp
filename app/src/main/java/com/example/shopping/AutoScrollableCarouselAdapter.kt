package com.example.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class AutoScrollableCarouselAdapter(private val list:List<Int>):RecyclerView.Adapter<AutoScrollableCarouselAdapter.ViewHolder>() {

    private lateinit var listener: ItemClickListener

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
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
        holder.imageView.setImageResource(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}