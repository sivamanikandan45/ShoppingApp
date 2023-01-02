package com.example.shopping.checkout.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shopping.checkout.enums.CheckoutMode
import com.example.shopping.address.model.Address
import com.example.shopping.cart.model.SelectedProduct

class CheckoutViewModel:ViewModel(){
    val selectedAddress=MutableLiveData<Address>()
    var selectedAddressPosition:Int=0
    var paymentMode:String=""
    var billAmount=0.0
    var mode: CheckoutMode = CheckoutMode.OVERALL
    var buyNowProductId:Int=0
    var buyNowProductQuantity=0
    var buyNowProduct: SelectedProduct? =null
}