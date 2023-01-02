package com.example.shopping.cart.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.product.model.Product
import com.example.shopping.cart.model.SelectedProduct
import com.example.shopping.cart.model.SelectedProductEntity
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
        println("prinln items in the cart ${cartItems.value}")
        for(product in cartItems.value!!){
            if(product.productId==productId){
                return true
            }
        }
        return false
    }
    fun getProductCount(productId: Int):Int{
        for(product in cartItems.value!!){
            if(product.productId==productId){
                return product.quantity
            }
        }
        return -1
    }

    fun selectedProduct(productId: Int): SelectedProduct?{
        for(product in cartItems.value!!){
            if(product.productId==productId){
                /*val productFromDB=getProductUsingSelectedProductIds(productId)
                val selectedProduct=(product.)*/
                return product
            }
        }
        return null
    }

    fun getCartFromDB(){
        viewModelScope.launch(Dispatchers.IO) {
            val selectedProductList= mutableListOf<SelectedProduct>()
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
            val productDao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
            val list=dao.getCartItems()
            println("price after updating ${dao.getCartAmountAfterDiscount()}")
            //cartItems.postValue(list)
            for(product in list){
                val productFromDb=productDao.getProduct(product.productId)
                val selectedProduct= SelectedProduct(product.productId,productFromDb.title,productFromDb.brand,productFromDb.thumbnail,product.oldPricePerProduct,product.discount,product.pricePerProduct,product.quantity,product.oldPriceForSelectedQuantity,product.priceForSelectedQuantity)
                selectedProductList.add(selectedProduct)
            }
            cartItems.postValue(selectedProductList)
            getCartItemCount()
            val after=getCartAmountAfterDiscount()
            val before=getCartAmountBeforeDiscount()
        }
        //discountAmount.postValue(before-after)
    }

    /*private fun updateCartAmount(amount: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                cartAmount.value=amount
            }
        }

    }*/

    fun addToCart(product: SelectedProductEntity){
        viewModelScope.launch(Dispatchers.IO) {
            val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
            dao.addItemToCart(product)
            getCartFromDB()
        }
        /*getCartAmount()*/
    }

    fun getCartItems():MutableList<SelectedProduct>{
        val selectedProductList= mutableListOf<SelectedProduct>()
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val productDao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        val list=dao.getCartItems()
        for(product in list){
            val productFromDb=productDao.getProduct(product.productId)
            val selectedProduct= SelectedProduct(product.productId,productFromDb.title,productFromDb.brand,productFromDb.thumbnail,product.oldPricePerProduct,product.discount,product.pricePerProduct,product.quantity,product.oldPriceForSelectedQuantity,product.priceForSelectedQuantity)
            selectedProductList.add(selectedProduct)
        }
        cartItems.postValue(selectedProductList)
        //cartItems.postValue(list)
        val after=getCartAmountAfterDiscount()
        val before=getCartAmountBeforeDiscount()
        return selectedProductList
    }

    fun clearCartItems(){
        viewModelScope.launch(Dispatchers.IO) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
            dao.clearAll()
            getCartFromDB()
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
            if(productId!=null){
                dao.removeItemFromCart(productId)
                getCartFromDB()
            }
        }
    }

    fun getCartItemCount():Int{
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
        val count=dao.getCartItemCount()
        noOfItem.postValue(count)
        return count
    }


    fun updateQuantity(product: SelectedProduct, quantity:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val productId=product.productId
            val pricePerProduct=product.pricePerProduct
            val oldPricePerProduct=product.oldPricePerProduct
            val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getCartDao()
            dao.updateProductQuantity(productId,quantity)
            dao.updatePriceForSelectedQuantity(productId,quantity*pricePerProduct)
            dao.updateOldPriceForSelectedQuantity(productId,quantity*oldPricePerProduct)
            //getCartFromDB()
            getCartAmountAfterDiscount()
            getCartAmountBeforeDiscount()
        }
    }

    fun getProductUsingSelectedProductIds(productId: Int):Product{
        val productList= mutableListOf<Product>()
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        val product=dao.getProduct(productId)
        return product
        /*for(recent in recentlyViewedProductIds){
            val product=dao.getProduct(recent.productId)
            productList.add(product)
        }
        recentlyViewedProductList.postValue(productList)*/
    }


}