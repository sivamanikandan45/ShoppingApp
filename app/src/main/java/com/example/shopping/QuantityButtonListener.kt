package com.example.shopping

import com.example.shopping.model.SelectedProduct

interface QuantityButtonListener {
    fun onIncreaseClicked(adapterPosition: Int)
    fun onDecreaseClicked(adapterPosition: Int)
}