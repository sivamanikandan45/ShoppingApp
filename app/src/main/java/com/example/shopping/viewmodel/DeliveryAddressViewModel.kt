package com.example.shopping.viewmodel

import androidx.lifecycle.ViewModel

class DeliveryAddressViewModel:ViewModel() {
    var name:String=""
    var phone:String=""
    var pinCode:Int?=null
    var state:String=""
    var city:String=""
    var street:String=""
    var area:String=""
}