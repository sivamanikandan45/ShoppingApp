package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.dao.FavoriteDao
import com.example.shopping.database.AppDB
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.SelectedProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application):AndroidViewModel(application) {

    var favoriteItems= MutableLiveData<List<FavoriteProduct>>()
    var calledFrom=""

    init {
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                getFavoriteListFromDB()
            }
            job.join()
        }
    }

    fun getFavoriteListFromDB() {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
            val list=dao.getFavoriteProductList()
            favoriteItems.postValue(list)
    }


    fun addToFavorites(product:FavoriteProduct){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
        dao.addToFavorite(product)
        getFavoriteListFromDB()
    }

    fun deleteFromFavorites(productId: Int?) {
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
        if(productId!=null){
            dao.removeFromFavorites(productId)
            getFavoriteListFromDB()
        }
        getFavoriteListFromDB()
    }

    suspend fun getWishlistItems():List<FavoriteProduct>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getFavoriteDao()
        val list=dao.getFavoriteProductList()
        favoriteItems.postValue(list)
        return list
    }

}