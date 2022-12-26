package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application):AndroidViewModel(application) {

    var favoriteItems= MutableLiveData<List<Product>>()
    var calledFrom=""
    private var currentUserId=-1

    init {
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                //getFavoriteListFromDB()
            }
            job.join()
        }
    }

    fun getProductListUsingFavoriteProductIds(recentlyViewedProductIds: List<FavoriteProduct>){
        val productList= mutableListOf<Product>()
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        for(recent in recentlyViewedProductIds){
            val product=dao.getProduct(recent.productId)
            productList.add(product)
        }
        favoriteItems.postValue(productList)
    }

    fun setUserId(userId:Int){
        currentUserId=userId
        viewModelScope.launch(Dispatchers.IO){
            val job=launch{
                getFavoriteListFromDB(currentUserId)
            }
            job.join()
        }
    }

    fun getFavoriteListFromDB(userId: Int) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
            val list=dao.getFavoriteProductList(userId)
            getProductListUsingFavoriteProductIds(list)
            //favoriteItems.postValue(favoriteProductList)
    }


    fun addToFavorites(product:FavoriteProduct){
        viewModelScope.launch(Dispatchers.IO) {
            val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
            dao.addToFavorite(product)
            getFavoriteListFromDB(currentUserId)
        }
    }

    fun deleteFromFavorites(productId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
            if(productId!=null){
                dao.removeFromFavorites(productId,currentUserId)
                getFavoriteListFromDB(currentUserId)
            }
            getFavoriteListFromDB(currentUserId)
        }
    }

    suspend fun getWishlistItems():List<FavoriteProduct>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
        val list=dao.getFavoriteProductList(currentUserId)
        getProductListUsingFavoriteProductIds(list)
        return list
    }

    fun isFavorite(productId: Int?):Boolean {
        if(favoriteItems.value!=null){
            for(product in favoriteItems.value!!){
                if(product.productId==productId){
                    return true
                }
            }
        }
        return false
    }

    suspend fun getIdOfFavorite(productId: Int, currentUserId: Int):Int {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
        val id=dao.getIdOfFavorite(productId,currentUserId)
        return id
    }

}