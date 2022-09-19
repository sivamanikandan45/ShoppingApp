package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.model.SelectedProduct

@Dao
interface CartDao {

    @Insert
    fun addItemToCart(item:SelectedProduct)

    @Query("SELECT * FROM SelectedProduct")
    fun getCartItems():MutableList<SelectedProduct>


}