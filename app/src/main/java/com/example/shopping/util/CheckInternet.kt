package com.example.shopping.util

import android.content.Context
import android.net.ConnectivityManager


object CheckInternet {
    fun isNetwork(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isConnectedNetwork(context: Context): Boolean {
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!
            .isConnectedOrConnecting
    }
}