package com.example.shopping

interface WishlistListener {
    fun addToCart(position:Int)
    fun removeItem(position: Int)
}