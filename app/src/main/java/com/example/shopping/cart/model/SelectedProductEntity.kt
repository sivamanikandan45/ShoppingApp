package com.example.shopping.cart.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.shopping.product.model.Product

@Entity(foreignKeys = [ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"])])
data class SelectedProductEntity (@PrimaryKey val productId:Int,
                                  var oldPricePerProduct: Double,
                                  val discount:Double,
                                  var pricePerProduct:Double,
                                  var quantity:Int,
                                  var oldPriceForSelectedQuantity:Double,
                                  var priceForSelectedQuantity:Double)

