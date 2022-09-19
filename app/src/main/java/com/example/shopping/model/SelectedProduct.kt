package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SelectedProduct (@PrimaryKey val productId:Int, val productName:String, val pricePerProduct:Double, val quantity:Int, val priceForSelectedQuantity:Double)