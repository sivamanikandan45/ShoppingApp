package com.example.shopping.order.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.shopping.product.model.Product

@Entity(foreignKeys = [ForeignKey(entity = Order::class, parentColumns = ["orderId"], childColumns = ["orderId"], onDelete = ForeignKey.CASCADE),
                        ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"])])
data class OrderedProductEntity(@PrimaryKey(autoGenerate = true)
                          val id:Int,
                                val orderId:Int,
                                val productId:Int,
                                val oldPriceForSelectedQuantity:Double,
                                val priceForSelectedQuantity:Double,
                                val discount:Double,
                                val quantity:Int)
