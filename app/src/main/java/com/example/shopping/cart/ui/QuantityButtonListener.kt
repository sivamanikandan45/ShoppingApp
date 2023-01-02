package com.example.shopping.cart.ui

interface QuantityButtonListener {
    fun onIncreaseClicked(adapterPosition: Int)
    fun onDecreaseClicked(adapterPosition: Int)
    fun updateQuantity(adapterPosition: Int)
}