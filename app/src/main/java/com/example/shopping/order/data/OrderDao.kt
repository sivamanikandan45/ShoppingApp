package com.example.shopping.order.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.order.model.Order

@Dao
interface OrderDao {

    @Insert
    fun placeOrder(order: Order):Long

    @Query("Select * FROM `order` where customerId = :userId")
    fun getOrderList(userId:Int):List<Order>

    @Query("SELECT orderId FROM `order` WHERE rowid = :rowId")
    fun getIdUsingRowId(rowId: Long):Int


}