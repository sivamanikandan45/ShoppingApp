package com.example.shopping.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(@PrimaryKey val productId:Int,
                   val title:String,
                   val description:String,
                   val originalPrice:Double,
                   val discountPercentage:Double,
                   val priceAfterDiscount:Double,
                   val rating:String,
                   val stock:Int,
                   val brand:String,
                   val category:String,
                   val thumbnail:String,
                   val favorite: Boolean =false){

    override fun equals(other: Any?): Boolean {
        if(other is Product){
            //println("comparting $this with $other")
            return when{
                this.productId!=other.productId-> false
                this.title!=other.title-> false
                this.description!=other.description-> false
                this.originalPrice!=other.originalPrice-> false
                this.discountPercentage!=other.discountPercentage-> false
                this.priceAfterDiscount!=other.priceAfterDiscount-> false
                this.rating!=other.rating-> false
                this.stock!=other.stock-> false
                this.brand!=other.brand-> false
                this.category!=other.category-> false
                this.thumbnail!=other.thumbnail-> false
                this.favorite!=other.favorite-> false
                else-> true
            }
        }
        else{
            return super.equals(other)
        }

    }
}

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