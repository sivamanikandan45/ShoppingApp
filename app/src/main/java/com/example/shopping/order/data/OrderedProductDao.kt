package com.example.shopping.order.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.order.model.OrderedProductEntity

@Dao
interface OrderedProductDao {
    @Insert
    fun addOrderedProduct(product: OrderedProductEntity)

    @Query("Select * FROM OrderedProductEntity where orderId=:orderId")
    fun getOrderedProduct(orderId:Int):List<OrderedProductEntity>

    /*@Query("SELECT thumbnail from OrderedProductEntity where orderId=:orderId")
    fun getOrderedProductImages(orderId: Int):List<String>*/
}