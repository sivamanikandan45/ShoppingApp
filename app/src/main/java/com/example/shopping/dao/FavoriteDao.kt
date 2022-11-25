package com.example.shopping.dao

import androidx.room.*
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product

@Dao
interface FavoriteDao {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    fun addToFavorite(favoriteProduct: FavoriteProduct)

    @Query("DELETE FROM FavoriteProduct where productId=:productId and customerId=:userId")
    fun removeFromFavorites(productId:Int,userId: Int)

    @Query("SELECT * FROM FavoriteProduct where customerId=:userId")
    fun getFavoriteProductList(userId:Int):List<FavoriteProduct>
}