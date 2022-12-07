package com.example.shopping.signinandsigupfeature.domain.repository

import com.example.shopping.signinandsigupfeature.data.local.entity.User

interface UserRepository {
    suspend fun registerUser(user: User):Long
    suspend fun isValidUser(email: String, password: String): Int
    suspend fun getIdUsingRowId(rowId:Long):Int
}