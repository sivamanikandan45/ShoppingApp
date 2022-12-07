package com.example.shopping.signinandsigupfeature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.signinandsigupfeature.data.local.entity.User

@Dao
interface UserDao {

    @Insert
    fun registerAccount(user: User):Long

    @Query("SELECT * from USER where email=:email and password=:password")
    fun getUser(email: String, password:String): User?

    @Query("SELECT userId FROM User WHERE rowid = :rowId")
    fun getIdUsingRowId(rowId: Long):Int

}