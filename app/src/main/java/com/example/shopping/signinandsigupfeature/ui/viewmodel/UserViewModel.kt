package com.example.shopping.signinandsigupfeature.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shopping.signinandsigupfeature.data.local.entity.User
import com.example.shopping.signinandsigupfeature.domain.usecase.CheckValidUserUseCase
import com.example.shopping.signinandsigupfeature.domain.usecase.GetIdUsingRowId
import com.example.shopping.signinandsigupfeature.domain.usecase.RegisterUserUseCase

class UserViewModel(private val registerUserUseCase: RegisterUserUseCase,
                    private val checkValidUserUseCase: CheckValidUserUseCase,
                    private val getIdUsingRowId: GetIdUsingRowId): ViewModel() {


    suspend fun registerUser(user: User):Long {
        /*val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        val rowId=dao.registerAccount(user)
        return rowId*/
        return registerUserUseCase.invoke(user)
    }

    suspend fun isValidUser(email: String, password: String): Int {
        /*val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        val user=dao.getUser(email,password)
        return user?.userId ?: -1*/
        return checkValidUserUseCase.invoke(email,password)
    }

    suspend fun getIdUsingRowId(rowId:Long):Int{
        /*val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        val id=dao.getIdUsingRowId(rowId)
        return id*/
        return  getIdUsingRowId.invoke(rowId)
    }


}