package com.example.shopping.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shopping.enums.Sort

class SortViewModel:ViewModel() {
    var selectedSort:Sort=Sort.NONE
}