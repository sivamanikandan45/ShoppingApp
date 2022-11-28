package com.example.shopping

import android.content.Context
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.MaterialAutoCompleteTextView


class ExposedDropDownMenu(context: Context,attributeSet: AttributeSet) : MaterialAutoCompleteTextView(context,attributeSet) {
    override fun getFreezesText(): Boolean {
        return false
    }
}