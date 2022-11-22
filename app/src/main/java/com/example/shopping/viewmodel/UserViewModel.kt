package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.shopping.database.AppDB
import com.example.shopping.model.User

class UserViewModel(application: Application):AndroidViewModel(application) {


    fun registerUser(user:User) {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        dao.registerAccount(user)
    }

    fun isValidUser(email: String, password: String): Int {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getUserDao()
        val user=dao.getUser(email,password)
        return user?.userId ?: -1
    }


}