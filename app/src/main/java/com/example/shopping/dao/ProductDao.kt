package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopping.model.Product

@Dao
interface ProductDao{

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insertProductList(list:List<Product>)

    @Query("SELECT * FROM Product")
    fun getProductList():List<Product>

    @Query("SELECT * FROM Product WHERE category=:category")
    fun getCategoryList(category:String):MutableList<Product>

    @Query("UPDATE Product SET favorite=1 WHERE productId=:productId")
    fun markAsFavorite(productId:Int)

    @Query("UPDATE Product SET favorite=0 WHERE productId=:productId")
    fun removeFavorite(productId:Int)



}