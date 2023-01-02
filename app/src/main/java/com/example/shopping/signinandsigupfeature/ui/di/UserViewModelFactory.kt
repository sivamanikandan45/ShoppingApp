package com.example.shopping.signinandsigupfeature.ui.di

import androidx.lifecycle.SavedStateHandle
import com.example.shopping.signinandsigupfeature.domain.usecase.CheckValidUserUseCase
import com.example.shopping.signinandsigupfeature.domain.usecase.GetIdUsingRowId
import com.example.shopping.signinandsigupfeature.domain.usecase.RegisterUserUseCase
import com.example.shopping.signinandsigupfeature.ui.viewmodel.UserViewModel

class UserViewModelFactory(private val registerUserUseCase: RegisterUserUseCase,
                           private val checkValidUserUseCase: CheckValidUserUseCase,
                           private val getIdUsingRowId: GetIdUsingRowId):Factory<UserViewModel> {
    override fun create(): UserViewModel? {
        //return UserViewModel(registerUserUseCase, checkValidUserUseCase, getIdUsingRowId,)
        return null
    }
}