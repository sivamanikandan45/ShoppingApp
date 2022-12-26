package com.example.shopping.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shopping.database.AppDB
import com.example.shopping.enums.FormMode
import com.example.shopping.model.Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddressViewModel(application: Application): AndroidViewModel(application){

    var addressList= MutableLiveData<List<Address>>()

    var formMode:FormMode = FormMode.CREATE
    val selectedAddress=MutableLiveData<Address>()
    private var currentUserId=-1

    init{
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                //getAddressFromDB()
            }
            job.join()
        }
    }

    fun setUserId(userId:Int){
        currentUserId=userId
        viewModelScope.launch{
            val job=launch (Dispatchers.IO){
                getAddressFromDB(userId)
                //getAddress(currentUserId)
            }
            job.join()
        }
    }

    fun getAddressFromDB(userId: Int) {
        println("fetching adressed of user $userId")
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
        val list=dao.getAllAddress(userId)
        println("fetched $list")
        addressList.postValue(list)
    }

    fun addAddress(address: Address){
        viewModelScope.launch(Dispatchers.IO) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
            dao.insertAddress(address)
            getAddressFromDB(currentUserId)
        }
    }

    fun deleteAddress(addressId:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
            dao.deleteAddress(addressId)
            getAddressFromDB(currentUserId)
        }
    }

    fun getAddressLists():List<Address>{
        var list= listOf<Address>()
        GlobalScope.launch{
            val job=launch(Dispatchers.IO) {
                val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
                list=dao.getAllAddress(currentUserId)
                addressList.postValue(list)
            }
            job.join()
        }
        return list
    }

    fun updateAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
            dao.updateAddress(address)
            getAddressFromDB(currentUserId)
        }
    }

    fun getAddress(addressId: Int):Address {
        val dao= AppDB.getDB(getApplication<Application?>().applicationContext).getAddressDao()
        val address=dao.getAddress(addressId)
        return address
    }





}