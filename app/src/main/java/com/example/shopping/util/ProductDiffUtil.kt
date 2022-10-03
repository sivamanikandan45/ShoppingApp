package com.example.shopping.util

import androidx.recyclerview.widget.DiffUtil
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product

class ProductDiffUtil (private val oldList:List<Product>, private val newList: List<Product>):
    DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].productId==newList[newItemPosition].productId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition])/*when{
            oldList[oldItemPosition].favorite!=newList[newItemPosition].favorite->{
                false
            }
            oldList[oldItemPosition].productId!=newList[newItemPosition].productId->false
            oldList[oldItemPosition].title!=newList[newItemPosition].title->false
            oldList[oldItemPosition].description!=newList[newItemPosition].description->false
            oldList[oldItemPosition].originalPrice!=newList[newItemPosition].originalPrice->false
            else-> {

                true
            }
        }*/
    }
}