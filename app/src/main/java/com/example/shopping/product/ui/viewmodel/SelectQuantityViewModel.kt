package com.example.shopping.product.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shopping.checkout.enums.CheckoutMode

class SelectQuantityViewModel:ViewModel() {
    var checkoutMode= CheckoutMode.OVERALL
}