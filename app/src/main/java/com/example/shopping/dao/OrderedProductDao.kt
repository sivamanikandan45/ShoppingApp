package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.model.OrderedProduct

@Dao
interface OrderedProductDao {
    @Insert
    fun addOrderedProduct(product: OrderedProduct)

    @Query("Select * FROM OrderedProduct where orderId=:orderId")
    fun getOrderedProduct(orderId:Int):List<OrderedProduct>

    @Query("SELECT thumbnail from OrderedProduct where orderId=:orderId")
    fun getOrderedProductImages(orderId: Int):List<String>
}