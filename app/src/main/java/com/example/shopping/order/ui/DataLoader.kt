package com.example.shopping.order.ui

import com.example.shopping.order.model.OrderedProduct

interface DataLoader {
    suspend fun loadData(orderId:Int):List<OrderedProduct>
}