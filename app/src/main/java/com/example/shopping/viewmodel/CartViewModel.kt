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
    var noOfItem=MutableLiveData<Int>()
    var cartAmountAfterDiscount=MutableLiveData<Double>()
    var cartAmountBeforeDiscount=MutableLiveData<Double>()
    //var discountAmount=MutableLiveData<Double>()


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

    fun isProductInCart(productId: Int):Boolean{
        for(product in cartItems.value!!){
            if(product.productId==productId){
                return true
            }
        }
        return false
    }

    fun getCartFromDB(){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val list=dao.getCartItems()
        println("price after updating ${dao.getCartAmountAfterDiscount()}")
        cartItems.postValue(list)
        getCartItemCount()
        val after=getCartAmountAfterDiscount()
        val before=getCartAmountBeforeDiscount()
        //discountAmount.postValue(before-after)
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
        /*getCartAmount()*/
    }

    fun getCartItems():MutableList<SelectedProduct>{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val list=dao.getCartItems()
        cartItems.postValue(list)
        return list
    }

    fun clearCartItems(){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        dao.clearAll()
        getCartFromDB()
    }

    fun getCartAmountAfterDiscount():Double{
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val price=dao.getCartAmountAfterDiscount()
        cartAmountAfterDiscount.postValue(price)
        return price
    }

    fun getCartAmountBeforeDiscount():Double{
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val price=dao.getCartAmountBeforeDiscount()
        cartAmountBeforeDiscount.postValue(price)
        return price
    }

    fun deleteProduct(productId: Int?) {
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        if(productId!=null){
            dao.removeItemFromCart(productId)
            getCartFromDB()
        }
    }

    fun getCartItemCount():Int{
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val count=dao.getCartItemCount()
        noOfItem.postValue(count)
        return count
    }


    fun updateQuantity(product: SelectedProduct,quantity:Int){
        val productId=product.productId
        val pricePerProduct=product.pricePerProduct
        val oldPricePerProduct=product.oldPricePerProduct
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        dao.updateProductQuantity(productId,quantity)
        dao.updatePriceForSelectedQuantity(productId,quantity*pricePerProduct)
        dao.updateOldPriceForSelectedQuantity(productId,quantity*oldPricePerProduct)
        getCartFromDB()
    }


}