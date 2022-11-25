package com.example.shopping.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["customerId"], onDelete = ForeignKey.CASCADE)])
data class FavoriteProduct(@PrimaryKey(autoGenerate = true)
val id:Int,
val productId:Int,
                           val customerId:Int,
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
)