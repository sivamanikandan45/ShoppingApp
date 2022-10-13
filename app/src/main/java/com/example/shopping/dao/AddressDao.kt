package com.example.shopping.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shopping.model.Address

@Dao
interface AddressDao {

    @Insert
    fun insertAddress(address: Address)

    @Query("SELECT * FROM ADDRESS")
    fun getAllAddress():List<Address>

    @Query("DELETE FROM ADDRESS where addressId=:addressId")
    fun deleteAddress(addressId:Int)

    @Update
    fun updateAddress(address: Address)

    @Query("SELECT * FROM Address where addressId=:addressId")
    fun getAddress(addressId: Int):Address
}