package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(@PrimaryKey(autoGenerate = true)
                 val orderId:Int, val customerName:String,
                 val customerPhone:String,
                 val pinCode:Int,
                 val state:String,
                 val city:String,
                 val street:String,
                 val area:String,
                 val itemCount:Int,
                 val originalTotalPrice:Double,
                 val discount:Double, val totalAfterDiscount:Double,
                 val orderedDate:String,
                 val expectedDeliveryDate:String,
                 val paymentMode:String)
