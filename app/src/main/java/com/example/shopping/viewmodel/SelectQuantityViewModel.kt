package com.example.shopping.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shopping.enums.CheckoutMode

class SelectQuantityViewModel:ViewModel() {
    var checkoutMode=CheckoutMode.OVERALL
}