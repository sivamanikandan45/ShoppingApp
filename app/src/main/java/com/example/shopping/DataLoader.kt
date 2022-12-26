package com.example.shopping

import com.example.shopping.model.OrderedProduct
import com.example.shopping.model.OrderedProductEntity

interface DataLoader {
    suspend fun loadData(orderId:Int):List<OrderedProduct>
}