package com.example.shopping.dao

import androidx.room.*
import com.example.shopping.model.RecentlyViewed

@Dao
interface RecentlyViewedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToRecentlyViewed(recentlyViewed: RecentlyViewed)


    @Query("DELEte FROM RecentlyViewed WHERE productId=:productId")
    fun deleteFromRecentlyViewed(productId:Int)


    @Query("SELECT * FROM RECENTLYVIEWED")
    fun getRecentlyViewedProducts():List<RecentlyViewed>

    @Query("Delete FROM RecentlyViewed where 1=1")
    fun clearAll()

    @Query("UPDATE RecentlyViewed SET isFavorite=:favorite WHERE productId=:productID")
    fun updateFavoriteStatusOfRecentlyViewed(favorite:Boolean,productID: Int)
}