package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.model.Order
import java.sql.RowId

@Dao
interface OrderDao {

    @Insert
    fun placeOrder(order: Order):Long

    @Query("Select * FROM `order`")
    fun getOrderList():List<Order>

    @Query("SELECT orderId FROM `order` WHERE rowid = :rowId")
    fun getIdUsingRowId(rowId: Long):Int


}