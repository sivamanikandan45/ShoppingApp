package com.example.shopping.address.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shopping.address.model.Address

@Dao
interface AddressDao {

    @Insert
    fun insertAddress(address: Address)

    @Query("SELECT * FROM ADDRESS where customerId= :userId")
    fun getAllAddress(userId:Int):List<Address>

    @Query("DELETE FROM ADDRESS where addressId=:addressId")
    fun deleteAddress(addressId:Int)

    @Update
    fun updateAddress(address: Address)

    @Query("SELECT * FROM Address where addressId=:addressId")
    fun getAddress(addressId: Int): Address
}