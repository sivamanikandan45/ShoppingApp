package com.example.shopping.dao

import androidx.room.*
import com.example.shopping.model.SelectedProduct

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItemToCart(item:SelectedProduct)

    @Query("SELECT * FROM SelectedProduct")
    fun getCartItems():MutableList<SelectedProduct>

    @Query("SELECT SUM(priceForSelectedQuantity) FROM SelectedProduct")
    fun getCartAmountAfterDiscount():Double

    @Query("DELETE FROM SelectedProduct where productId=:productId")
    fun removeItemFromCart(productId:Int)

    @Query("SELECT COUNT(*) FROM SelectedProduct")
    fun getCartItemCount():Int

    @Query("SELECT SUM(olcPriceForSelectedQuantity) FROM SelectedProduct")
    fun getCartAmountBeforeDiscount():Double

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCartItem(item:SelectedProduct)

    @Query("UPDATE SelectedProduct SET quantity=:quantity where productId=:id")
    fun updateProductQuantity(id:Int,quantity:Int)

    @Query("UPDATE SelectedProduct SET priceForSelectedQuantity=:priceForSelectedQuantity where productId=:id")
    fun updatePriceForSelectedQuantity(id:Int,priceForSelectedQuantity:Double)

    @Query("UPDATE SelectedProduct SET olcPriceForSelectedQuantity=:oldPriceForSelectedQuantity where productId=:id")
    fun updateOldPriceForSelectedQuantity(id:Int, oldPriceForSelectedQuantity:Double)
}