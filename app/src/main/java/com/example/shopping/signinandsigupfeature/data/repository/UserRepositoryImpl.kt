package com.example.shopping.signinandsigupfeature.data.repository

import com.example.shopping.signinandsigupfeature.data.local.UserLocalDataSource
import com.example.shopping.signinandsigupfeature.data.local.entity.User
import com.example.shopping.signinandsigupfeature.domain.repository.UserRepository

class UserRepositoryImpl(private val userLocalDataSource: UserLocalDataSource):UserRepository {
    override suspend fun registerUser(user: User): Long {
        return userLocalDataSource.registerUser(user)
    }

    override suspend fun isValidUser(email: String, password: String): Int {
        return userLocalDataSource.isValidUser(email,password)
    }

    override suspend fun getIdUsingRowId(rowId: Long): Int {
        return userLocalDataSource.getIdUsingRowId(rowId)
    }
}