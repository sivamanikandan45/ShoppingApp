package com.example.shopping

import com.example.shopping.model.OrderedProduct

interface ImageLoader {
    suspend fun loadImage(orderId:Int):List<String>
}