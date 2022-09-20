package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.model.SelectedProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(application: Application):AndroidViewModel(application) {

    var cartItems=MutableLiveData<List<SelectedProduct>>()
    var noOfItem=MutableLiveData<Int>()
    //var cartAmount:MutableLiveData<Double> = MutableLiveData()

    init {
        //cartAmount.value=0.0
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                getCartFromDB()
            }
            job.join()
            println(cartItems)
            noOfItem.value=cartItems.value?.size?:0
        }
    }

    fun getCartFromDB(){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val list=dao.getCartItems()
        println("price after updating ${dao.getCartAmount()}")
        cartItems.postValue(list)
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                //noOfItem.value=cartItems.value?.size?:0
                noOfItem.value=cartItems.value?.size?:0
            }
        }

        //updateCartAmount(dao.getCartAmount())
        //cartAmount.postValue(dao.getCartAmount())
    }

    /*private fun updateCartAmount(amount: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                cartAmount.value=amount
            }
        }

    }*/

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