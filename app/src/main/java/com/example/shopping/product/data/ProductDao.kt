package com.example.shopping.product.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopping.product.model.Product

@Dao
interface ProductDao{

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insertProductList(list:List<Product>)

    @Query("SELECT * FROM Product")
    fun getProductList():List<Product>

    @Query("SELECT * FROM Product WHERE category=:category")
    fun getCategoryList(category:String):MutableList<Product>

    /*@Query("UPDATE Product SET favorite=1 WHERE productId=:productId")
    fun markAsFavorite(productId:Int)

    @Query("UPDATE Product SET favorite=0 WHERE productId=:productId")
    fun removeFavorite(productId:Int)*/

    @Query("SELECT * FROM Product ORDER BY discountPercentage DESC LIMIT 10")
    fun getTopOffers():List<Product>

    @Query("SELECT * FROM Product where productId=:id")
    fun getProduct(id:Int):Product




}