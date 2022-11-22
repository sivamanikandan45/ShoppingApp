package com.example.shopping.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shopping.dao.*
import com.example.shopping.model.*

@Database(entities = [User::class,Product::class,CarouselImage::class,SelectedProduct::class,FavoriteProduct::class,RecentlyViewed::class,Address::class,Order::class,OrderedProduct::class], version = 1)
abstract class AppDB :RoomDatabase(){
    abstract fun getUserDao():UserDao
    abstract fun getProductDao():ProductDao
    abstract fun getImageDao():ImageDao
    abstract fun getCartDao():CartDao
    abstract fun getFavoriteDao():FavoriteDao
    abstract fun getRecentlyViewedDao():RecentlyViewedDao
    abstract fun getAddressDao():AddressDao
    abstract fun getOrderDao():OrderDao
    abstract fun getOrderedProductDao():OrderedProductDao

    companion object{
        @Volatile
        private var instance:AppDB?=null
        fun getDB(context: Context?):AppDB{
            val temp= instance
            if(temp != null){
                return temp
            }
            synchronized(this){
                val newInstance= Room.databaseBuilder(context!!, AppDB::class.java, "ProductDatabase").build()
                instance=newInstance
                return newInstance
            }
        }
    }
}