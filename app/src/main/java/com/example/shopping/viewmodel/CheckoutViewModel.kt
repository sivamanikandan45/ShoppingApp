package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shopping.database.AppDB
import com.example.shopping.model.Address
import com.example.shopping.model.Order

class CheckoutViewModel:ViewModel(){
    val selectedAddress=MutableLiveData<Address>()
    var paymentMode:String=""
}