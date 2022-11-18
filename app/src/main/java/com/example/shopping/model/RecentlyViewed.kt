package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentlyViewed(@PrimaryKey val productId:Int,
                          val title:String,
                          val description:String,
                          val originalPrice:Double,
                          val discountPercentage:Double,
                          val priceAfterDiscount:Double,
                          val rating:String,
                          val stock:Int,
                          val brand:String,
                          val category:String,
                          val thumbnail:String,
                          val isFavorite:Boolean)
