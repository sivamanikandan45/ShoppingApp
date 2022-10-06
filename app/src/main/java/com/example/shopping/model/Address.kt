package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    @PrimaryKey(autoGenerate = true)
    val addressId:Int,
    val name:String,
    val phone:String,
    val pinCode:Int,
    val state:String,
    val city:String,
    val street:String,
    val area:String)
