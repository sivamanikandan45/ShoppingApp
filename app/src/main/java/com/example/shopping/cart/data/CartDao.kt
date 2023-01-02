package com.example.shopping.cart.data

import androidx.room.*
import com.example.shopping.cart.model.SelectedProductEntity

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItemToCart(item: SelectedProductEntity)

    @Query("SELECT * FROM SelectedProductEntity")
    fun getCartItems():MutableList<SelectedProductEntity>

    @Query("SELECT SUM(priceForSelectedQuantity) FROM SelectedProductEntity")
    fun getCartAmountAfterDiscount():Double

    @Query("DELETE FROM SelectedProductEntity where productId=:productId")
    fun removeItemFromCart(productId:Int)

    @Query("SELECT COUNT(*) FROM SelectedProductEntity")
    fun getCartItemCount():Int

    @Query("SELECT SUM(oldPriceForSelectedQuantity) FROM SelectedProductEntity")
    fun getCartAmountBeforeDiscount():Double

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCartItem(item: SelectedProductEntity)

    @Query("UPDATE SelectedProductEntity SET quantity=:quantity where productId=:id")
    fun updateProductQuantity(id:Int,quantity:Int)

    @Query("UPDATE SelectedProductEntity SET priceForSelectedQuantity=:priceForSelectedQuantity where productId=:id")
    fun updatePriceForSelectedQuantity(id:Int,priceForSelectedQuantity:Double)

    @Query("UPDATE SelectedProductEntity SET oldPriceForSelectedQuantity=:oldPriceForSelectedQuantity where productId=:id")
    fun updateOldPriceForSelectedQuantity(id:Int, oldPriceForSelectedQuantity:Double)

    @Query("Delete FROM SelectedProductEntity where 1=1")
    fun clearAll()
}