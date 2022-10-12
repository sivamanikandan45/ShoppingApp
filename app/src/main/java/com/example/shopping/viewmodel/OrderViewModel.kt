package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.shopping.database.AppDB
import com.example.shopping.model.Order
import com.example.shopping.model.OrderedProduct

class OrderViewModel(application: Application): AndroidViewModel(application){

    fun placeOrder(order: Order):Long{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val rowId=dao.placeOrder(order)
        return rowId
    }

    fun getIdUsingRowId(rowId:Long):Int{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val id=dao.getIdUsingRowId(rowId)
        return id
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


}