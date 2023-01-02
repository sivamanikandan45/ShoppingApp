package com.example.shopping.cart.model

import androidx.room.PrimaryKey

data class SelectedProduct (val productId:Int,
                            val productName:String,
                            val productBrand:String,
                            val imageUrl:String,
                            var oldPricePerProduct: Double,
                            val discount:Double,
                            var pricePerProduct:Double,
                            var quantity:Int,
                            var oldPriceForSelectedQuantity:Double,
                            var priceForSelectedQuantity:Double)