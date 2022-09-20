package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopping.model.SelectedProduct

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItemToCart(item:SelectedProduct)

    @Query("SELECT * FROM SelectedProduct")
    fun getCartItems():MutableList<SelectedProduct>

    @Query("SELECT SUM(priceForSelectedQuantity) FROM SelectedProduct")
    fun getCartAmount():Double

}