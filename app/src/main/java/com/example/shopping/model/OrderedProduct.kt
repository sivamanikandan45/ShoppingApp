package com.example.shopping.model

data class OrderedProduct(val id:Int,
                          val orderId:Int,
                          val productId:Int,
                          val productName:String,
                          val brand:String,
                          val oldPriceForSelectedQuantity:Double,
                          val priceForSelectedQuantity:Double,
                          val discount:Double,
                          val quantity:Int,
                          val thumbnail:String)
