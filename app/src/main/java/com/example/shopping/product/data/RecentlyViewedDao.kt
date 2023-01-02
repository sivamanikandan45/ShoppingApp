package com.example.shopping.product.data

import androidx.room.*
import com.example.shopping.product.model.RecentlyViewed

@Dao
interface RecentlyViewedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToRecentlyViewed(recentlyViewed: RecentlyViewed)


    @Query("DELEte FROM RecentlyViewed WHERE productId=:productId")
    fun deleteFromRecentlyViewed(productId:Int)


    @Query("SELECT * FROM RECENTLYVIEWED ORDER BY recentlyViewedId DESC")
    fun getRecentlyViewedProducts():List<RecentlyViewed>

    @Query("Delete FROM RecentlyViewed where 1=1")
    fun clearAll()

    /*@Query("UPDATE RecentlyViewed SET isFavorite=:favorite WHERE productId=:productID")
    fun updateFavoriteStatusOfRecentlyViewed(favorite:Boolean,productID: Int)*/
}