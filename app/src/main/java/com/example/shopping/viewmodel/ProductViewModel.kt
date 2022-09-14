package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.CategoryType
import com.example.shopping.database.ProductDB
import com.example.shopping.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ProductViewModel(application: Application):AndroidViewModel(application) {
    var productList = MutableLiveData<List<Product>>()
    var categoryType:CategoryType=CategoryType.MEN
    var categoryList = MutableLiveData<List<Product>>()


    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                getALlProducts()
            }
            if(productList.value?.isEmpty() == true){
                val job=launch {
                    println("loading from api")
                    loadData()
                }
                job.join()
            }
        }
    }

    private fun getALlProducts() {
        val dao=ProductDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        val list=dao.getProductList()
        productList.postValue(list)
    }

    private suspend fun loadData() {
        withContext(Dispatchers.IO){
            val url = "https://dummyjson.com/products?skip=5&limit=100"
            val connection = URL(url).openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var response = ""
            var line = reader.readLine()
            while (line != null) {
                response += line
                line = reader.readLine()
            }

            if (response.isNotEmpty()) {
                val list:MutableList<Product> = mutableListOf()
                val jsonObject = JSONTokener(response).nextValue() as JSONObject
                val jsonArray = jsonObject.getJSONArray("products")
                for (i in 0 until jsonArray.length()) {

                    val productId:Int=jsonArray.getJSONObject(i).getString("id").toInt()
                    val title:String=jsonArray.getJSONObject(i).getString("title")
                    val description:String=jsonArray.getJSONObject(i).getString("description")
                    val originalPrice:Double=jsonArray.getJSONObject(i).getString("price").toDouble()
                    val discountPercentage:Double=jsonArray.getJSONObject(i).getString("discountPercentage").toDouble()
                    val priceAfterDiscount:Double=jsonArray.getJSONObject(i).getString("price").toDouble()
                    val rating:Float=jsonArray.getJSONObject(i).getString("rating").toFloat()
                    val stock:Int=jsonArray.getJSONObject(i).getString("stock").toInt()
                    val brand:String=jsonArray.getJSONObject(i).getString("brand")
                    val category:String=jsonArray.getJSONObject(i).getString("category")
                    val thumbnail:String=jsonArray.getJSONObject(i).getString("thumbnail")
                    //val images:List<String> = listOf(jsonArray.getJSONObject(i).getString("images"))

                    val product=Product(productId, title, description, originalPrice, discountPercentage, priceAfterDiscount, rating, stock, brand, category, thumbnail)
                    list.add(product)
                }
                insertProductListToDB(list)
                println(list)
                println(jsonArray.getJSONObject(0).getString("title"))
            }
        }
    }

    private fun insertProductListToDB(list: MutableList<Product>) {
        val dao=ProductDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        dao.insertProductList(list)
        getALlProducts()
    }

    fun getCategoryWiseProductList():List<Product>{
        var list= listOf<Product>()

        println("from viewmodel : $categoryType")

        viewModelScope.launch {
            val job = launch {
                withContext(Dispatchers.IO) {
                    val dao = ProductDB.getDB(getApplication<Application?>().applicationContext)
                        .getProductDao()
                    list = dao.getProductList()
                    productList.postValue(list)
                }
            }
            job.join()


            /*when(categoryType){
                CategoryType.MEN->{
                        val job=launch {
                            val list1 = getCategoryFromDB("men-shirts")
                            *//*val list2 = getCategoryFromDB("mens-shoes")
                            val list3 = getCategoryFromDB("mens-watches")
                            list = list1 + list2 + list3
                            println(list1)*//*
                        }
                    job.join()
                }
                CategoryType.WOMEN->{
                        val job=launch {
                            *//*val list1 = getCategoryFromDB("womens-dresses")
                            val list2 = getCategoryFromDB("womens-watches")
                            val list3 = getCategoryFromDB("womens-shoes")
                            val list4 = getCategoryFromDB("womens-bags")
                            val list5 = getCategoryFromDB("womens-jwellery")
                            list = list1 + list2 + list3 + list4 + list5*//*
                        }
                    job.join()
                }
                CategoryType.ELECTRONICS->{
                        val job=launch {
                            //list=getCategoryFromDB("laptops")
                        }
                        job.join()
                }
                CategoryType.FURNITURE->{
                        val job=launch {
                            *//*val list1=getCategoryFromDB("furniture")
                            val list2=getCategoryFromDB("home-decoration")
                            list=list1+list2*//*
                        }
                        job.join()
                }
                CategoryType.SUNGLASSES->{
                        val job=launch {
                            //list=getCategoryFromDB("sunglasses")
                        }
                        job.join()
                }
                CategoryType.GROCERIES->{
                        val job=launch {
                            //list=getCategoryFromDB("groceries")
                        }
                        job.join()
                }
                CategoryType.BEAUTY->{
                        val job=launch {
                            *//*val list1=getCategoryFromDB("skincare")
                            val list2=getCategoryFromDB("fragrances")
                            list=list1+list2*//*
                        }
                        job.join()
                }
                CategoryType.OTHERS->{
                        val job=launch {
                            *//*val list1=getCategoryFromDB("tops")
                            val list2=getCategoryFromDB("automotive")
                            val list3=getCategoryFromDB("motorcycle")
                            val list4=getCategoryFromDB("lighting")
                            list=list1+list2+list3+list4*//*
                        }
                        job.join()
                }
            }
        }
        categoryList.postValue(list)
        println("category list is ${categoryList.value}")*/
        }
        return list
    }

    suspend fun getCategoryFromDB(category:String):List<Product>{
        var list = listOf<Product>()
        withContext(Dispatchers.IO){
            val dao=ProductDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
            list=dao.getCategoryList()
            println(list)
        }
        return list
    }

}