package com.example.shopping.signinandsigupfeature.data.local

import com.example.shopping.signinandsigupfeature.data.local.dao.UserDao
import com.example.shopping.signinandsigupfeature.data.local.entity.User

class UserLocalDataSource(private val userDao:UserDao) {

    suspend fun registerUser(user: User):Long {
        return userDao.registerAccount(user)
    }

    suspend fun isValidUser(email: String, password: String):Int {
        val user=userDao.getUser(email,password)
        return user?.userId ?: -1
    }

    fun getIdUsingRowId(rowId: Long): Int {
        val id=userDao.getIdUsingRowId(rowId)
        return id
    }
}