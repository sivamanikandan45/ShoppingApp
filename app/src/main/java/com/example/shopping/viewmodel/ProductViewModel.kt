package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.enums.CategoryType
import com.example.shopping.database.AppDB
import com.example.shopping.model.CarouselImage
import com.example.shopping.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.Comparator

class ProductViewModel(application: Application):AndroidViewModel(application) {
    var productList = MutableLiveData<List<Product>>()
    var categoryType: CategoryType = CategoryType.MEN
    var categoryList = MutableLiveData<List<Product>>()

    var selectedProduct=MutableLiveData<Product>()
    val topOfferList= MutableLiveData<List<Product>>()

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
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        val list=dao.getProductList()
        productList.postValue(list)
        getTopOfferList()
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
                    val title:String=jsonArray.getJSONObject(i).getString("title").capitalize()
                    val description:String=jsonArray.getJSONObject(i).getString("description").capitalize()
                    val originalPrice:Double=jsonArray.getJSONObject(i).getString("price").toDouble()
                    val discountPercentage:Double=jsonArray.getJSONObject(i).getString("discountPercentage").toDouble()
                    var priceAfterDiscount:Double=jsonArray.getJSONObject(i).getString("price").toDouble()
                    val rating=jsonArray.getJSONObject(i).getString("rating")
                    val stock:Int=jsonArray.getJSONObject(i).getString("stock").toInt()
                    val brand:String=jsonArray.getJSONObject(i).getString("brand")
                    val category:String=jsonArray.getJSONObject(i).getString("category")
                    val thumbnail:String=jsonArray.getJSONObject(i).getString("thumbnail")
                    var images = jsonArray.getJSONObject(i).getString("images")

                    val images1=images.replace("[","")
                    val images2 =images1.replace("]","")
                    val images3=images2.replace("\"","")
                    val images4=images3.replace("\\/","/")

                    val imagesArray=images4.split(",")
                    for(link in imagesArray){
                        val carouselImage=CarouselImage(0,productId,link)
                        addImageToDB(carouselImage)
                    }


                    //println(images)

                    //val array=images.split("https:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
                    //println(array)

                    //println("images is $images")
                    /*images.replace("https:\\/","https://")
                    println("images is $images")*/

                    /*for(i in 0 until images.length()){
                        //val carouselImage=CarouselImage(0,productId,i.toString())
                        println(i.toString())
                    }*/

                    priceAfterDiscount=originalPrice-originalPrice*(discountPercentage/100)
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.DOWN
                    priceAfterDiscount = df.format(priceAfterDiscount).toDouble()

                    val product=Product(productId, title, description, originalPrice, discountPercentage, priceAfterDiscount, rating, stock, brand, category, thumbnail)
                    list.add(product)
                }
                insertProductListToDB(list)
                println(list)
                println(jsonArray.getJSONObject(0).getString("title"))
            }
        }
    }

    private fun addImageToDB(carouselImage: CarouselImage) {
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getImageDao()
        dao.insertImage(carouselImage)
    }

    fun getImageUrlList(productId:Int):MutableList<String>{
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getImageDao()
        val list=dao.getImageURL(productId)
        return list
    }

    private fun insertProductListToDB(list: MutableList<Product>) {
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        dao.insertProductList(list)
        getALlProducts()
    }

    fun getCategoryWiseProductList():List<Product>{
        var list= listOf<Product>()
        viewModelScope.launch {
            val job = launch {
                withContext(Dispatchers.IO) {
                    val dao = AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
                    list = dao.getProductList()
                    productList.postValue(list)
                }
            }
            job.join()
            when(categoryType){
                CategoryType.MEN->{
                    val job=launch {
                        val shirtList = getCategoryFromDB("mens-shirts")
                        val shoesList = getCategoryFromDB("mens-shoes")
                        val watchList = getCategoryFromDB("mens-watches")
                        list = shirtList + shoesList + watchList
                        categoryList.postValue(list)
                    }
                    job.join()
                }
                CategoryType.WOMEN->{
                    val job=launch {
                        val dressList = getCategoryFromDB("womens-dresses")
                        val watchList = getCategoryFromDB("womens-watches")
                        val shoesList = getCategoryFromDB("womens-shoes")
                        val bagsList = getCategoryFromDB("womens-bags")
                        val jewelleryList = getCategoryFromDB("womens-jewellery")
                        list = dressList + watchList + shoesList + bagsList + jewelleryList
                        categoryList.postValue(list)
                    }
                    job.join()
                }
                CategoryType.ELECTRONICS->{
                    val job=launch {
                        list=getCategoryFromDB("laptops")
                        categoryList.postValue(list)
                    }
                    job.join()
                }

                CategoryType.FURNITURE->{
                    val job=launch {
                        val furnitureList=getCategoryFromDB("furniture")
                        val homeDecorList=getCategoryFromDB("home-decoration")
                        list=furnitureList+homeDecorList
                        categoryList.postValue(list)
                    }
                    job.join()
                }
                CategoryType.SUNGLASSES->{
                    val job=launch {
                        list=getCategoryFromDB("sunglasses")
                        categoryList.postValue(list)
                    }
                    job.join()
                }
                CategoryType.GROCERIES->{
                    val job=launch {
                        list=getCategoryFromDB("groceries")
                        categoryList.postValue(list)
                    }
                    job.join()
                }
                CategoryType.BEAUTY->{
                    val job=launch {
                        val skincareList=getCategoryFromDB("skincare")
                        val list2=getCategoryFromDB("fragrances")
                        list=skincareList+list2
                        categoryList.postValue(list)
                    }
                    job.join()
                }
                CategoryType.OTHERS->{
                    val job=launch {
                        val list1=getCategoryFromDB("tops")
                        val list2=getCategoryFromDB("automotive")
                        val list3=getCategoryFromDB("motorcycle")
                        val list4=getCategoryFromDB("lighting")
                        list=list1+list2+list3+list4
                        categoryList.postValue(list)
                    }
                    categoryList.postValue(list)
                    job.join()
                }
            }
        }
        categoryList.postValue(list)
        return list
    }

    suspend fun getCategoryFromDB(category:String):MutableList<Product>{
        var list = mutableListOf<Product>()
        withContext(Dispatchers.IO){
            val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
            list=dao.getCategoryList(category)
        }
        return list
    }

    fun sortByAlphabet() {
        Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.title?.compareTo(p1.title) ?: 0
                }
                return 0
            }
        })
        /*for(item in categoryList.value!!){
            println(item.title)
        }*/
    }

    fun sortByARating() {
        Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.rating?.compareTo(p1.rating) ?: 0
                }
                return 0
            }
        })
        categoryList.postValue(categoryList.value!!.reversed())
    }

    fun sortByPriceHghToLow() {
        Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.priceAfterDiscount?.compareTo(p1.priceAfterDiscount) ?: 0
                }
                return 0
            }
        })
        categoryList.postValue(categoryList.value!!.reversed())
    }

    fun sortByPriceLowToHigh() {
        Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.priceAfterDiscount?.compareTo(p1.priceAfterDiscount) ?: 0
                }
                return 0
            }
        })
    }

    fun markAsFavorite(productId: Int){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        dao.markAsFavorite(productId)
        getALlProducts()
        getCategoryWiseProductList()
    }

    fun removeFavorite(productId: Int){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        dao.removeFavorite(productId)
        getALlProducts()
        getCategoryWiseProductList()
    }

    fun getTopOfferList(){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        val list=dao.getTopOffers()
        topOfferList.postValue(list)
    }



}