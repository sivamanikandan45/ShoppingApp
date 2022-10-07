package com.example.shopping.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shopping.model.Address

class CheckoutViewModel:ViewModel(){
    val selectedAddress=MutableLiveData<Address>()
}