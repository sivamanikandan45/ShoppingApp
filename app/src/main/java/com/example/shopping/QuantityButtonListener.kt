package com.example.shopping

interface QuantityButtonListener {
    fun onIncreaseClicked(adapterPosition: Int)
    fun onDecreaseClicked(adapterPosition: Int)
    fun updateQuantity(adapterPosition: Int)
}