package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SelectedProduct (@PrimaryKey val productId:Int,
                            val productName:String,
                            val productBrand:String,
                            val imageUrl:String,
                            var oldPricePerProduct: Double,
                            val discount:Double,
                            var pricePerProduct:Double,
                            var quantity:Int,
                            var oldPriceForSelectedQuantity:Double,
                            var priceForSelectedQuantity:Double)
