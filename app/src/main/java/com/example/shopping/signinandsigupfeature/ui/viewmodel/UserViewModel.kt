package com.example.shopping.signinandsigupfeature.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.shopping.MyApplication
import com.example.shopping.signinandsigupfeature.data.local.entity.User
import com.example.shopping.signinandsigupfeature.domain.usecase.CheckValidUserUseCase
import com.example.shopping.signinandsigupfeature.domain.usecase.GetIdUsingRowId
import com.example.shopping.signinandsigupfeature.domain.usecase.RegisterUserUseCase

class UserViewModel(private val registerUserUseCase: RegisterUserUseCase,
                    private val checkValidUserUseCase: CheckValidUserUseCase,
                    private val getIdUsingRowId: GetIdUsingRowId,
                    ): ViewModel() {

    var state=false


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

    companion object{
        /*val Factory:ViewModelProvider.Factory = object:ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return UserViewModel(
                    (application as MyApplication).appContainer.registerUserUseCase,
                    (application as MyApplication).appContainer.checkValidUserUseCase,
                    (application as MyApplication).appContainer.getIdUsingRowId,
                    savedStateHandle
                ) as T
               // return super.create(modelClass, extras)
            }
        }*/

        val Factory:ViewModelProvider.Factory= viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val myApplication=(this[APPLICATION_KEY] as MyApplication).appContainer
                val registerUserUseCase=myApplication.registerUserUseCase
                val checkValidUserUseCase=myApplication.checkValidUserUseCase
                val getIdUsingRowId=myApplication.getIdUsingRowId
                UserViewModel(registerUserUseCase = registerUserUseCase,
                    checkValidUserUseCase = checkValidUserUseCase,
                    getIdUsingRowId = getIdUsingRowId)
            }
        }
    }


}