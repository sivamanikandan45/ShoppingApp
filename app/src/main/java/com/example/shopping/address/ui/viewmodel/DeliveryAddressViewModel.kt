package com.example.shopping.address.ui.viewmodel

import androidx.lifecycle.ViewModel

class DeliveryAddressViewModel:ViewModel() {
    var isModified=false
    var name:String=""
    var phone:String=""
    var pinCode:Int?=null
    var state:String=""
    var city:String=""
    var street:String=""
    var area:String=""
}