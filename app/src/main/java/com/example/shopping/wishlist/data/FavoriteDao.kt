package com.example.shopping.wishlist.data

import androidx.room.*
import com.example.shopping.wishlist.model.FavoriteProduct

@Dao
interface FavoriteDao {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    fun addToFavorite(favoriteProduct: FavoriteProduct)

    @Query("DELETE FROM FavoriteProduct where productId=:productId and customerId=:userId")
    fun removeFromFavorites(productId:Int,userId: Int)

    @Query("SELECT * FROM FavoriteProduct where customerId=:userId")
    fun getFavoriteProductList(userId:Int):List<FavoriteProduct>

    @Query("SELECT id FROM FavoriteProduct where customerId=:userId and productId=:productId")
    fun getIdOfFavorite(productId: Int,userId: Int):Int
}