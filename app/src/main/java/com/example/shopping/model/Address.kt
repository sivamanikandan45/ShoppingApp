package com.example.shopping.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["customerId"], onDelete = ForeignKey.CASCADE)])
data class Address(
    @PrimaryKey(autoGenerate = true)
    val addressId:Int,
    val customerId:Int,
    val name:String,
    val phone:String,
    val pinCode:Int,
    val state:String,
    val city:String,
    val street:String,
    val area:String)
