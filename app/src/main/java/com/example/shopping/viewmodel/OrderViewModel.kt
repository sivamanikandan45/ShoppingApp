package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.model.Address
import com.example.shopping.model.Order
import com.example.shopping.model.OrderedProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OrderViewModel(application: Application): AndroidViewModel(application){
    var orderList= MutableLiveData<List<Order>>()
    val selectedOrder=MutableLiveData<Order>()

    init{
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                getOrdersFromDB()
            }
            job.join()
        }
    }

    fun getOrdersFromDB() {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val list=dao.getOrderList()
        orderList.postValue(list)
    }

    fun placeOrder(order: Order):Long{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val rowId=dao.placeOrder(order)
        getOrdersFromDB()
        return rowId
    }

    fun getIdUsingRowId(rowId:Long):Int{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val id=dao.getIdUsingRowId(rowId)
        return id
    }

    fun getOrderList():List<Order>{
        var list= listOf<Order>()
        GlobalScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
                list=dao.getOrderList()
            }
            job.join()
        }
        return list
    }

    fun getOrders():List<Order>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val list=dao.getOrderList()
        return list
    }



    fun addOrderedProduct(product: OrderedProduct){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderedProductDao()
        dao.addOrderedProduct(product)
    }

    fun getOrderedProduct(orderId:Int):List<OrderedProduct>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderedProductDao()
        val list=dao.getOrderedProduct(orderId)
        return list
    }

    fun getOrderedProductImages(orderId:Int):List<String>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderedProductDao()
        val list=dao.getOrderedProductImages(orderId)
        return list
    }


}