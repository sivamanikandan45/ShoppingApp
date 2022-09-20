package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.model.SelectedProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(application: Application):AndroidViewModel(application) {

    var cartItems=MutableLiveData<List<SelectedProduct>>()

    init {
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                getCartFromDB()
            }
            job.join()
            println(cartItems)
        }
    }

    fun getCartFromDB(){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val list=dao.getCartItems()
        cartItems.postValue(list)
    }

    fun addToCart(product:SelectedProduct){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        dao.addItemToCart(product)
        getCartFromDB()
    }

    fun getCartItems():MutableList<SelectedProduct>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val list=dao.getCartItems()
        cartItems.postValue(list)
        return list
    }

}