package com.example.shopping.product.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopping.product.model.CarouselImage

@Dao
interface ImageDao {

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun insertImages(list:List<CarouselImage>)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun insertImage(imageObject:CarouselImage)

   @Query("SELECT * FROM CarouselImage WHERE productId=:productId")
   fun getImageURL(productId:Int):MutableList<CarouselImage>

}