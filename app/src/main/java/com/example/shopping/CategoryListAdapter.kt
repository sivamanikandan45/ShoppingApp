package com.example.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Category
import com.google.android.material.imageview.ShapeableImageView

class CategoryListAdapter(val list:List<Category>):RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val categoryImageView:ShapeableImageView
        val categoryName: TextView
        init {
            categoryImageView=view.findViewById(R.id.category_image)
            categoryName=view.findViewById(R.id.category_name)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_category,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryName.text=list[position].categoryName
        holder.categoryImageView.setImageResource(list[position].imageId)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}