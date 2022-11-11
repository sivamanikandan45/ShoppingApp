package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.enums.CategoryType
import com.example.shopping.database.AppDB
import com.example.shopping.enums.Sort
import com.example.shopping.model.CarouselImage
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import kotlinx.coroutines.*
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
    var searchedQuery:String=""
    var selectedSort: Sort = Sort.NONE

    //var productStack=Stack<Product>()
    /*fun push(product:Product){
        productStack.push(product)
        selectedProduct.value=productStack.peek()
    }

    fun pop(){
        productStack.pop()
        selectedProduct.value=productStack.peek()
    }*/

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
                    var originalPrice:Double=jsonArray.getJSONObject(i).getString("price").toDouble()
                    originalPrice *= 82.25
                    val discountPercentage:Double=jsonArray.getJSONObject(i).getString("discountPercentage").toDouble()
                    var priceAfterDiscount:Double=jsonArray.getJSONObject(i).getString("price").toDouble()
                    priceAfterDiscount*=82.25
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

    fun getImageUrlList(productId:Int):MutableList<CarouselImage>{
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
                        sortList()
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
                        sortList()
                    }
                    job.join()
                }
                CategoryType.ELECTRONICS->{
                    val job=launch {
                        list=getCategoryFromDB("laptops")
                        categoryList.postValue(list)
                        sortList()
                    }
                    job.join()
                }

                CategoryType.FURNITURE->{
                    val job=launch {
                        val furnitureList=getCategoryFromDB("furniture")
                        val homeDecorList=getCategoryFromDB("home-decoration")
                        list=furnitureList+homeDecorList
                        categoryList.postValue(list)
                        sortList()
                    }
                    job.join()
                }
                CategoryType.SUNGLASSES->{
                    val job=launch {
                        list=getCategoryFromDB("sunglasses")
                        categoryList.postValue(list)
                        sortList()
                    }
                    job.join()
                }
                CategoryType.GROCERIES->{
                    val job=launch {
                        list=getCategoryFromDB("groceries")
                        categoryList.postValue(list)
                        sortList()
                    }
                    job.join()
                }
                CategoryType.BEAUTY->{
                    val job=launch {
                        val skincareList=getCategoryFromDB("skincare")
                        val perfumeList=getCategoryFromDB("fragrances")
                        list=skincareList+perfumeList
                        categoryList.postValue(list)
                        sortList()
                    }
                    job.join()
                }
                CategoryType.OTHERS->{
                    val job=launch {
                        val dressList=getCategoryFromDB("tops")
                        val automotiveList=getCategoryFromDB("automotive")
                        val motorcycleList=getCategoryFromDB("motorcycle")
                        val lightingList=getCategoryFromDB("lighting")
                        list=dressList+automotiveList+motorcycleList+lightingList
                        categoryList.postValue(list)
                        sortList()
                    }
                    //categoryList.postValue(list)
                    job.join()
                }
            }
        }
        //categoryList.postValue(list)
        println("Changed list is $list")
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
        /*Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.title?.compareTo(p1.title) ?: 0
                }
                return 0
            }
        })*/


        val newList=categoryList.value?.sortedBy {
            it.title
        }
        categoryList.postValue(newList!!)
        //categoryList.value=newList!!

        /*for(item in categoryList.value!!){
            println(item.title)
        }*/
    }

    fun sortByARating() {
        /*Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.rating?.compareTo(p1.rating) ?: 0
                }
                return 0
            }
        })
        categoryList.postValue(categoryList.value!!.reversed())*/
        val newList=categoryList.value?.sortedByDescending {
            it.rating
        }
        //categoryList.value=newList!!
        categoryList.postValue(newList!!)

    }

    fun sortByPriceHghToLow() {
        /*Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.priceAfterDiscount?.compareTo(p1.priceAfterDiscount) ?: 0
                }
                return 0
            }
        })
        categoryList.postValue(categoryList.value!!.reversed())*/
        val newList=categoryList.value?.sortedByDescending {
            it.priceAfterDiscount
        }
        categoryList.value=newList!!
    }

    fun sortByPriceLowToHigh() {
        /*Collections.sort(categoryList.value!!,object :Comparator<Product>{
            override fun compare(p0: Product?, p1: Product?): Int {
                if (p1 != null) {
                    return p0?.priceAfterDiscount?.compareTo(p1.priceAfterDiscount) ?: 0
                }
                return 0
            }
        })*/
        val newList=categoryList.value?.sortedBy{
            it.priceAfterDiscount
        }
        categoryList.value=newList!!
    }

    fun markAsFavorite(productId: Int){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        dao.markAsFavorite(productId)
        getALlProducts()
        //getCategoryWiseProductList()
    }

    fun removeFavorite(productId: Int){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        dao.removeFavorite(productId)
        getALlProducts()
        //getCategoryWiseProductList()
    }

    fun getTopOfferList(){
        val dao=AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
        val list=dao.getTopOffers()
        topOfferList.postValue(list)
    }

    fun getTopOfferFromDB():List<Product>{
        var list= listOf<Product>()
        GlobalScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
                list=dao.getTopOffers()
            }
            job.join()
        }
        return list
    }

    fun getAllProducts():List<Product>{
        var list= listOf<Product>()
        GlobalScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
                list=dao.getProductList()
            }
            job.join()
        }
        return list
    }

    fun getProductByID(id: Int){
        GlobalScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getProductDao()
                val product=dao.getProduct(id)
                selectedProduct.postValue(product)
            }
            job.join()
        }
    }

    fun sortList(){
        when(selectedSort){
            Sort.NONE->{

            }
            Sort.ALPHA->{
                sortByAlphabet()
            }
            Sort.RATING->{
                sortByARating()
            }
            Sort.PRICE_HIGH_TO_LOW->{
                sortByPriceHghToLow()
            }
            Sort.PRICE_LOW_TO_HIGH->{
                sortByPriceLowToHigh()
            }
        }
    }


}