package com.example.shopping.order.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.order.model.Order
import com.example.shopping.order.model.OrderedProduct
import com.example.shopping.order.model.OrderedProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(application: Application): AndroidViewModel(application){
    var orderList= MutableLiveData<List<Order>>()
    val selectedOrder=MutableLiveData<Order>()
    private var currentUserId=-1
    var selectedId=-1
    var fromCheckOutPage=false

    init{
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                //getOrdersFromDB()
            }
            job.join()
        }
    }

    fun setUserId(userId:Int) {
        this.currentUserId=userId
        viewModelScope.launch(Dispatchers.IO){
            val job=launch{
                getOrdersFromDB(currentUserId)
            }
            job.join()
        }
    }

    fun getOrdersFromDB(userId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
            val list=dao.getOrderList(userId)
            orderList.postValue(list)
        }
    }

    fun placeOrder(order: Order):Long{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val rowId=dao.placeOrder(order)
        getOrdersFromDB(currentUserId)
        return rowId
    }

    fun getIdUsingRowId(rowId:Long):Int{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val id=dao.getIdUsingRowId(rowId)
        return id
    }

    fun getOrderList():List<Order>{
        var list= listOf<Order>()
        viewModelScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
                list=dao.getOrderList(currentUserId)
            }
            job.join()
        }
        return list
    }

    fun getOrders():List<Order>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderDao()
        val list=dao.getOrderList(currentUserId)
        return list
    }



    fun addOrderedProduct(product: OrderedProductEntity){
        viewModelScope.launch (Dispatchers.IO){
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderedProductDao()
            dao.addOrderedProduct(product)
        }
    }

    fun getOrderedProduct(orderId:Int):List<OrderedProduct>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderedProductDao()
        val productDao= AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        val list=dao.getOrderedProduct(orderId)
        val orderedProductList= mutableListOf<OrderedProduct>()
       for(product in list){
           val productFromDB=productDao.getProduct(product.productId)
           val orderedProduct= OrderedProduct(product.id,product.orderId,product.productId,productFromDB.title,productFromDB.brand,product.oldPriceForSelectedQuantity,product.priceForSelectedQuantity,product.discount,product.quantity,productFromDB.thumbnail)
           orderedProductList.add(orderedProduct)
       }
        return orderedProductList
    }

    /*fun getOrderedProductImages(orderId:Int):List<String>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getOrderedProductDao()
        val list=dao.getOrderedProductImages(orderId)
        return list
    }*/

    fun getOrder(orderId: Int): Order? {
        println("Searching for $orderId")
        println("The current order list is")
        println(orderList.value)
        for(order in orderList.value!!){
            println(order)
            if(orderId==order.orderId){
                println("The placed order is $order")
                return order
            }
        }
        println("nothing found")
        return null
    }


}