package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CarouselImage(@PrimaryKey(autoGenerate = true) val imageId:Int, val productId:Int, val imageUrl:String)
