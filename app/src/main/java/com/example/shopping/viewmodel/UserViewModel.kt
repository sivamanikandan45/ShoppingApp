package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.shopping.database.AppDB
import com.example.shopping.model.User

class UserViewModel(application: Application):AndroidViewModel(application) {


    fun registerUser(user:User):Long {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        val rowId=dao.registerAccount(user)
        return rowId
    }

    fun isValidUser(email: String, password: String): Int {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        val user=dao.getUser(email,password)
        return user?.userId ?: -1
    }

    fun getIdUsingRowId(rowId:Long):Int{
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        val id=dao.getIdUsingRowId(rowId)
        return id
    }


}