package com.example.shopping.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shopping.enums.CheckoutMode
import com.example.shopping.model.Address
import com.example.shopping.model.SelectedProduct
import com.example.shopping.model.SelectedProductEntity

class CheckoutViewModel:ViewModel(){
    val selectedAddress=MutableLiveData<Address>()
    var selectedAddressPosition:Int=0
    var paymentMode:String=""
    var billAmount=0.0
    var mode:CheckoutMode=CheckoutMode.OVERALL
    var buyNowProductId:Int=0
    var buyNowProductQuantity=0
    var buyNowProduct: SelectedProduct? =null
}