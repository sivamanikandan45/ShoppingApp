package com.example.shopping.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shopping.address.data.AddressDao
import com.example.shopping.address.model.Address
import com.example.shopping.cart.data.CartDao
import com.example.shopping.cart.model.SelectedProductEntity
import com.example.shopping.signinandsigupfeature.data.local.dao.UserDao
import com.example.shopping.signinandsigupfeature.data.local.entity.User
import com.example.shopping.product.model.*
import com.example.shopping.order.data.OrderDao
import com.example.shopping.order.data.OrderedProductDao
import com.example.shopping.order.model.Order
import com.example.shopping.order.model.OrderedProductEntity
import com.example.shopping.product.data.ImageDao
import com.example.shopping.product.data.ProductDao
import com.example.shopping.product.data.RecentlyViewedDao
import com.example.shopping.wishlist.data.FavoriteDao
import com.example.shopping.wishlist.model.FavoriteProduct

@Database(entities = [User::class,Product::class,CarouselImage::class, SelectedProductEntity::class, FavoriteProduct::class,RecentlyViewed::class, Address::class, Order::class, OrderedProductEntity::class], version = 1)
abstract class AppDB :RoomDatabase(){
    abstract fun getUserDao(): UserDao
    abstract fun getProductDao(): ProductDao
    abstract fun getImageDao(): ImageDao
    abstract fun getCartDao(): CartDao
    abstract fun getFavoriteDao(): FavoriteDao
    abstract fun getRecentlyViewedDao(): RecentlyViewedDao
    abstract fun getAddressDao(): AddressDao
    abstract fun getOrderDao(): OrderDao
    abstract fun getOrderedProductDao(): OrderedProductDao

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