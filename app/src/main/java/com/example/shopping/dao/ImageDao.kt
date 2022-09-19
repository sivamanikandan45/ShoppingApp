package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopping.model.CarouselImage
import com.example.shopping.model.Product

@Dao
interface ImageDao {

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun insertImages(list:List<CarouselImage>)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun insertImage(imageObject:CarouselImage)

   @Query("SELECT imageUrl FROM CarouselImage WHERE productId=:productId")
   fun getImageURL(productId:Int):MutableList<String>



   //@Query("SELECT * FROM CarouselImage WHERE productId=:productId")
   //fun getmages(productId:Int):List<CarouselImage>
}