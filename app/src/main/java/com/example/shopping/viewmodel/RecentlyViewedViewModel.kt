package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.model.RecentlyViewed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecentlyViewedViewModel(application: Application):AndroidViewModel(application) {

    var recentlyViewedProductList= MutableLiveData<List<RecentlyViewed>>()

    init{
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                getRecentlyViewedFromDB()
            }
            job.join()
        }
    }


    fun getRecentlyViewedFromDB(){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
        val list=dao.getRecentlyViewedProducts()
        recentlyViewedProductList.postValue(list)
    }

    fun addToRecentlyViewed(product:RecentlyViewed){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
        dao.addToRecentlyViewed(product)
        getRecentlyViewedFromDB()
    }

    fun deleteFromRecentlyViewed(productID: Int){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
        dao.deleteFromRecentlyViewed(productID)
        getRecentlyViewedFromDB()
    }

    fun clearAllRecentlyViewed(){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
        dao.clearAll()
        getRecentlyViewedFromDB()
    }

    fun recentlyViewedItems():List<RecentlyViewed>{
        var list= listOf<RecentlyViewed>()
        GlobalScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getRecentlyViewedDao()
                list=dao.getRecentlyViewedProducts()
            }
            job.join()
        }
       return list
    }

}