package com.example.shopping.wishlist.ui

interface WishlistListener {
    fun moveToCart(position:Int)
    fun removeItem(position: Int)
}