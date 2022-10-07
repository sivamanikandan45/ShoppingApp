package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.model.Address
import com.example.shopping.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddressViewModel(application: Application): AndroidViewModel(application){

    var addressList= MutableLiveData<List<Address>>()

    init{
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                getAddressFromDB()
            }
            job.join()
        }
    }

    fun getAddressFromDB() {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
        val list=dao.getAllAddress()
        addressList.postValue(list)
    }

    fun addAddress(address: Address){
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
        dao.insertAddress(address)
        getAddressFromDB()
    }

    fun getAddressLists():List<Address>{
        var list= listOf<Address>()
        GlobalScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
                list=dao.getAllAddress()
            }
            job.join()
        }
        return list
    }

}