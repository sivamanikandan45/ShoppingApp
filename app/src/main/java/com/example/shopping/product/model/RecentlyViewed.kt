package com.example.shopping.product.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"], onDelete = ForeignKey.CASCADE)], indices = [Index(value=["productId"], unique = true)])
data class RecentlyViewed(@PrimaryKey(autoGenerate = true) val recentlyViewedId:Int,
                          val productId:Int)
