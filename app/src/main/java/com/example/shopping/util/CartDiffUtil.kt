package com.example.shopping.util

import androidx.recyclerview.widget.DiffUtil
import com.example.shopping.model.SelectedProduct

class CartDiffUtil(private val oldList:List<SelectedProduct>, private val newList: List<SelectedProduct>):DiffUtil.Callback() {
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
        return when{
            oldList[oldItemPosition].quantity!=newList[newItemPosition].quantity->false
            else->true
        }
    }
}