package com.example.shopping.product.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.product.model.Product
import com.example.shopping.product.model.RecentlyViewed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentlyViewedViewModel(application: Application):AndroidViewModel(application) {

    var recentlyViewedProductList= MutableLiveData<List<Product>>()
    //var recentlyViewedProducts=MutableLiveData<List<Product>>()

    init{
        viewModelScope.launch(Dispatchers.IO){
            getRecentlyViewedFromDB()
        }
    }


    fun getRecentlyViewedFromDB(){
        viewModelScope.launch(Dispatchers.IO) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
            val list=dao.getRecentlyViewedProducts()
            getProductListUsingRecentlyUsedProductIds(list)
        }
        //recentlyViewedProductList.postValue(list)
    }

    fun addToRecentlyViewed(product:RecentlyViewed){
        viewModelScope.launch (Dispatchers.IO){
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
            dao.addToRecentlyViewed(product)
            getRecentlyViewedFromDB()
        }
    }

    fun deleteFromRecentlyViewed(productID: Int){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
        dao.deleteFromRecentlyViewed(productID)
        getRecentlyViewedFromDB()
    }

    fun clearAllRecentlyViewed(){
        viewModelScope.launch(Dispatchers.IO) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
            dao.clearAll()
            getRecentlyViewedFromDB()
            println("changed recentlyviewed items")
        }
    }

    fun recentlyViewedItems():List<Product>{
        var list= listOf<Product>()
        viewModelScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
                val recentlist=dao.getRecentlyViewedProducts()
                getProductListUsingRecentlyUsedProductIds(recentlist)
            }
            job.join()
        }
       return list
    }


    fun getProductListUsingRecentlyUsedProductIds(recentlyViewedProductIds: List<RecentlyViewed>){
        val productList= mutableListOf<Product>()
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        for(recent in recentlyViewedProductIds){
            val product=dao.getProduct(recent.productId)
            productList.add(product)
        }
        recentlyViewedProductList.postValue(productList)
        println("recentlyviwed list is set to $productList}")
        println("saved recentlyviwed list  ${recentlyViewedProductList.value}")
    }

    /*fun updateFavoriteStatus(status:Boolean,productID: Int){
        viewModelScope.launch(Dispatchers.IO){
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
            dao.updateFavoriteStatusOfRecentlyViewed(status,productID)
            getRecentlyViewedFromDB()
        }
    }*/

}