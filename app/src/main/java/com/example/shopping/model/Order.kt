package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(@PrimaryKey(autoGenerate = true)
                 val orderId:Int, val addressId:Int,
                 val originalTotalPrice:Double,
                 val discount:Double, val totalAfterDiscount:Double,
                 val orderedDate:String,
                 val expectedDeliveryDate:String)
