package com.example.shopping.signinandsigupfeature.ui.di

import com.example.shopping.signinandsigupfeature.domain.usecase.CheckValidUserUseCase
import com.example.shopping.signinandsigupfeature.domain.usecase.GetIdUsingRowId
import com.example.shopping.signinandsigupfeature.domain.usecase.RegisterUserUseCase

class UserContainer(registerUserUseCase: RegisterUserUseCase,checkValidUserUseCase: CheckValidUserUseCase,getIdUsingRowId: GetIdUsingRowId) {
    val userViewModelFactory=UserViewModelFactory(registerUserUseCase, checkValidUserUseCase, getIdUsingRowId)
}