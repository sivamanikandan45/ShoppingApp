package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product

@Dao
interface FavoriteDao {

    @Insert
    fun addToFavorite(favoriteProduct: FavoriteProduct)

    @Query("DELETE FROM FavoriteProduct where productId=:productId")
    fun removeFromFavorites(productId:Int)

    @Query("SELECT * FROM FavoriteProduct")
    fun getFavoriteProductList():List<FavoriteProduct>
}