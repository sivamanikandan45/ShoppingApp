package com.example.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Product
import com.squareup.picasso.Picasso

class ProductListAdapter:RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    private lateinit var list:ArrayList<Product>

    fun setData(list:ArrayList<Product>){
        this.list=list
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val imageView:ImageView
        val productNameTextView:TextView
        val productBrandTextView:TextView
        val productPriceTextView:TextView
        init {
            imageView=view.findViewById(R.id.product_card_imageview)
            productNameTextView=view.findViewById(R.id.productCard_ProductName)
            productPriceTextView=view.findViewById(R.id.product_card_price)
            productBrandTextView=view.findViewById(R.id.productCard_brand)
        }

        fun bind(product: Product) {
            productNameTextView.text=product.title
            Picasso.get().load(product.thumbnail).into(imageView);
            productPriceTextView.text=product.originalPrice.toString()
            productBrandTextView.text=product.brand
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.product_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}