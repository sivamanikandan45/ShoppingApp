package com.example.shopping.signinandsigupfeature.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(@PrimaryKey(autoGenerate = true) val userId:Int, val email:String, val password:String)