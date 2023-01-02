package com.example.shopping.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomViewModel:ViewModel() {
    var fragmentId=0
    var fragmentName=MutableLiveData<String>()
    init {
        fragmentName.value="Home"
    }
}