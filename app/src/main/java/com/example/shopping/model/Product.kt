package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(@PrimaryKey val productId:Int,
                   val title:String,
                   val description:String,
                   val price:String,
                   val discountPercentage:String,
                   val rating:String,
                   val stock:String,
                   val brand:String,
                   val category: String,
                   val thumbnail:String,
                   val images:List<String>)

/*id
* title
* desc
* price
* discountPercentage
* rating
* stock
* brand
* category
* thumbnail
* images array
* */