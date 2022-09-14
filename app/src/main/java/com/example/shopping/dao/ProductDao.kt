package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopping.model.Category
import com.example.shopping.model.Product

@Dao
interface ProductDao{

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insertProductList(list:List<Product>)

    @Query("SELECT * FROM Product")
    fun getProductList():List<Product>
    //select email from mytable group by email having count(*) >1
    //select email from mytable group by email having count(*) >1

    @Query("SELECT * FROM Product WHERE category='mens-shirts'")
    fun getCategoryList():List<Product>

}