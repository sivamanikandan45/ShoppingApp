package com.example.shopping

interface WishlistListener {
    fun moveToCart(position:Int)
    fun removeItem(position: Int)
}