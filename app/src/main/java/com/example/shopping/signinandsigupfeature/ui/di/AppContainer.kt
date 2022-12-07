package com.example.shopping.signinandsigupfeature.ui.di

import android.content.Context
import com.example.shopping.database.AppDB
import com.example.shopping.signinandsigupfeature.data.local.UserLocalDataSource
import com.example.shopping.signinandsigupfeature.data.repository.UserRepositoryImpl
import com.example.shopping.signinandsigupfeature.domain.repository.UserRepository
import com.example.shopping.signinandsigupfeature.domain.usecase.CheckValidUserUseCase
import com.example.shopping.signinandsigupfeature.domain.usecase.GetIdUsingRowId
import com.example.shopping.signinandsigupfeature.domain.usecase.RegisterUserUseCase

class AppContainer(context: Context) {
    var userContainer:UserContainer?=null
    val userDao=AppDB.getDB(context).getUserDao()
    val userLocalDataSource=UserLocalDataSource(userDao)
    val userRepositoryImpl:UserRepository=UserRepositoryImpl(userLocalDataSource)

    val checkValidUserUseCase=CheckValidUserUseCase(userRepositoryImpl)
    val getIdUsingRowId=GetIdUsingRowId(userRepositoryImpl)
    val registerUserUseCase=RegisterUserUseCase(userRepositoryImpl)

}