package com.example.shopping.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.shopping.signinandsigupfeature.data.local.entity.User

@Entity(foreignKeys = [ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"])])
data class SelectedProductEntity (@PrimaryKey val productId:Int,
                                  var oldPricePerProduct: Double,
                                  val discount:Double,
                                  var pricePerProduct:Double,
                                  var quantity:Int,
                                  var oldPriceForSelectedQuantity:Double,
                                  var priceForSelectedQuantity:Double)

