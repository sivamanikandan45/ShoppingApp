package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SelectedProduct (@PrimaryKey val productId:Int,
                            val productName:String,
                            val productBrand:String,
                            val imageUrl:String,
                            val oldPricePerProduct: Double,
                            val discount:Double,
                            val pricePerProduct:Double,
                            val quantity:Int,
                            val oldPriceForSelectedQuantity:Double,
                            val priceForSelectedQuantity:Double)
