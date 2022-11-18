package com.example.shopping

import com.example.shopping.model.OrderedProduct

interface DataLoader {
    suspend fun loadData(orderId:Int):List<OrderedProduct>
}