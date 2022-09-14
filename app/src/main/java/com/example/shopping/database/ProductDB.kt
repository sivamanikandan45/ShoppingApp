package com.example.shopping.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shopping.dao.ProductDao
import com.example.shopping.model.Product

@Database(entities = [Product::class], version = 1)
abstract class ProductDB :RoomDatabase(){
    abstract fun getProductDao():ProductDao

    companion object{
        @Volatile
        private var instance:ProductDB?=null
        fun getDB(context: Context?):ProductDB{
            val temp= instance
            if(temp != null){
                return temp
            }
            synchronized(this){
                val newInstance= Room.databaseBuilder(context!!, ProductDB::class.java, "ProductDatabase").build()
                instance=newInstance
                return newInstance
            }
        }
    }
}