package com.example.shopping.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shopping.dao.CartDao
import com.example.shopping.dao.FavoriteDao
import com.example.shopping.dao.ImageDao
import com.example.shopping.dao.ProductDao
import com.example.shopping.model.CarouselImage
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import com.example.shopping.model.SelectedProduct

@Database(entities = [Product::class,CarouselImage::class,SelectedProduct::class,FavoriteProduct::class], version = 1)
abstract class AppDB :RoomDatabase(){
    abstract fun getProductDao():ProductDao
    abstract fun getImageDao():ImageDao
    abstract fun getCartDao():CartDao
    abstract fun getFavoriteDao():FavoriteDao

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