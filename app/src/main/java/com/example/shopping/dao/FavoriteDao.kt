package com.example.shopping.dao

import androidx.room.*
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product

@Dao
interface FavoriteDao {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    fun addToFavorite(favoriteProduct: FavoriteProduct)

    @Query("DELETE FROM FavoriteProduct where productId=:productId")
    fun removeFromFavorites(productId:Int)

    @Query("SELECT * FROM FavoriteProduct")
    fun getFavoriteProductList():List<FavoriteProduct>
}