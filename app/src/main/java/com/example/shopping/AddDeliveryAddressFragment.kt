package com.example.shopping

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.shopping.enums.FormMode
import com.example.shopping.model.Address
import com.example.shopping.viewmodel.AddressViewModel
import com.example.shopping.viewmodel.DeliveryAddressViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDeliveryAddressFragment : Fragment() {
    private val addressViewModel:AddressViewModel by activityViewModels()
    private val deliveryAddressViewModel:DeliveryAddressViewModel by viewModels()
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout
    private lateinit var pinCodeInputLayout: TextInputLayout
    private lateinit var stateInputLayout: TextInputLayout
    private lateinit var cityInputLayout: TextInputLayout
    private lateinit var addressStreetInputLayout: TextInputLayout
    private lateinit var areaInputLayout: TextInputLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN+WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )*/

        return inflater.inflate(R.layout.fragment_add_delivery_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("View created again")
        println("Fetched ${deliveryAddressViewModel.city} ${deliveryAddressViewModel.pinCode}")
        (activity as AppCompatActivity).supportActionBar?.title="Add Delivery Address"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameInputLayout=view.findViewById<TextInputLayout>(R.id.fullname)
        phoneInputLayout=view.findViewById<TextInputLayout>(R.id.phn)
        pinCodeInputLayout=view.findViewById<TextInputLayout>(R.id.pincode)
        stateInputLayout=view.findViewById<TextInputLayout>(R.id.state)
        cityInputLayout=view.findViewById<TextInputLayout>(R.id.city)
        addressStreetInputLayout=view.findViewById<TextInputLayout>(R.id.addressline1)
        areaInputLayout=view.findViewById<TextInputLayout>(R.id.addressline2)

        if(addressViewModel.formMode==FormMode.EDIT){
            (activity as AppCompatActivity).supportActionBar?.title="Edit Delivery Address"
            val selectedAddress=addressViewModel.selectedAddress.value
            nameInputLayout.editText?.setText(selectedAddress?.name!!)
            phoneInputLayout.editText?.setText(selectedAddress?.phone)
            selectedAddress?.pinCode?.let { pinCodeInputLayout.editText?.setText(it.toString()) }
            stateInputLayout.editText?.setText(selectedAddress?.state)
            cityInputLayout.editText?.setText(selectedAddress?.city)
            addressStreetInputLayout.editText?.setText(selectedAddress?.street)
            areaInputLayout.editText?.setText(selectedAddress?.area)
        }

        if(deliveryAddressViewModel.name!=""){
            println("Activated")
            nameInputLayout.editText?.setText(deliveryAddressViewModel.name)
        }
        if(deliveryAddressViewModel.phone!=""){
            phoneInputLayout.editText?.setText(deliveryAddressViewModel.phone)
        }
        if(deliveryAddressViewModel.pinCode!=null){
            deliveryAddressViewModel.pinCode?.let { pinCodeInputLayout.editText?.setText(it.toString()) }
        }
        if(deliveryAddressViewModel.state!=""){
            stateInputLayout.editText?.setText(deliveryAddressViewModel.state)
        }
        if(deliveryAddressViewModel.city!=""){
            cityInputLayout.editText?.setText(deliveryAddressViewModel.city)
        }
        if(deliveryAddressViewModel.street!=""){
            addressStreetInputLayout.editText?.setText(deliveryAddressViewModel.street)
        }
        if(deliveryAddressViewModel.area!=""){
            areaInputLayout.editText?.setText(deliveryAddressViewModel.area)
        }

        val saveAddressButton=view.findViewById<Button>(R.id.save_address)
        saveAddressButton.setOnClickListener {
            if(validateInputs()){
                val name=nameInputLayout.editText?.text.toString()
                val phone=phoneInputLayout.editText?.text.toString()
                val pinCode=pinCodeInputLayout.editText?.text.toString().toInt()
                val state=stateInputLayout.editText?.text.toString()
                val city=cityInputLayout.editText?.text.toString()
                val street=addressStreetInputLayout.editText?.text.toString()
                val area=areaInputLayout.editText?.text.toString()
                if(addressViewModel.formMode==FormMode.CREATE){
                    val address=Address(0,name,phone,pinCode,state,city,street, area)
                    saveAddress(address)
                }
                if(addressViewModel.formMode==FormMode.EDIT){
                    val id=addressViewModel.selectedAddress.value?.addressId!!
                    val address=Address(id,name,phone,pinCode,state,city,street, area)
                    updateAddress(address)
                }

            }else{
                println("Some input error")
            }
        }
    }

    private fun saveAddress(address: Address) {
        GlobalScope.launch {
            val job=launch (Dispatchers.IO){
                addressViewModel.addAddress(address)
            }
            job.join()
            withContext(Dispatchers.Main){
                activity?.onBackPressed()
            }
        }
    }

    private fun updateAddress(address: Address) {
        GlobalScope.launch {
            val job=launch (Dispatchers.IO){
                addressViewModel.updateAddress(address)
            }
            job.join()
            withContext(Dispatchers.Main){
                activity?.onBackPressed()
            }
        }
    }

    private fun validateInputs():Boolean {
        var returnValue=true
        val name=view?.findViewById<TextInputLayout>(R.id.fullname)
        val phone=view?.findViewById<TextInputLayout>(R.id.phn)
        val pinCode=view?.findViewById<TextInputLayout>(R.id.pincode)
        val state=view?.findViewById<TextInputLayout>(R.id.state)
        val city=view?.findViewById<TextInputLayout>(R.id.city)
        val addressStreet=view?.findViewById<TextInputLayout>(R.id.addressline1)
        val area=view?.findViewById<TextInputLayout>(R.id.addressline2)

        if(name!=null){
            if(name.editText?.text.toString()==""){
                name.isErrorEnabled =true
                name.error ="Please Enter your Name"
                returnValue=returnValue and false
            }else{
                name.error =null
                name.isErrorEnabled =false
            }
        }

        if(phone!=null){
            if(phone.editText?.text.toString()==""){
                phone.isErrorEnabled =true
                phone.error ="Please Enter your Phone Number"
                returnValue=returnValue and false
            }else if(phone.editText?.text.toString().length!=10){
                phone.isErrorEnabled =true
                phone.error ="Please Enter Valid Phone Number"
                returnValue=returnValue and false
            }else{
                phone.error =null
                phone.isErrorEnabled =false
            }
        }

        if(pinCode!=null){
            if(pinCode.editText?.text.toString()==""){
                pinCode.isErrorEnabled =true
                pinCode.error ="Please Enter your Pin code"
                returnValue=returnValue and false
            }else if(pinCode.editText?.text.toString().startsWith("0")){
                pinCode.isErrorEnabled =true
                pinCode.error ="Pincode should not start with 0"
                returnValue=returnValue and false
            }
            else if(pinCode.editText?.text.toString().length<6){
                pinCode.isErrorEnabled =true
                pinCode.error ="Invalid Pincode"
                returnValue=returnValue and false
            }else{
                pinCode.error =null
                pinCode.isErrorEnabled =false
            }

        }

        if(state!=null){
            if(state.editText?.text.toString()==""){
                state.isErrorEnabled =true
                state.error ="Please Enter your State"
                returnValue=returnValue and false
            }else{
                state.error =null
                state.isErrorEnabled =false
            }
        }

        if(city!=null){
            if(city.editText?.text.toString()==""){
                city.isErrorEnabled =true
                city.error ="Please Enter your City"
                returnValue=returnValue and false
            }else{
                city.error =null
                city.isErrorEnabled =false
            }
        }

        if(addressStreet!=null){
            if(addressStreet.editText?.text.toString()==""){
                addressStreet.isErrorEnabled =true
                addressStreet.error ="Please Enter your Address"
                returnValue=returnValue and false
            }else if(!addressStreet.editText?.text.toString().contains(",")){
                addressStreet.isErrorEnabled =true
                addressStreet.error ="Please Provide more details"
                returnValue=returnValue and false
            }
            else{
                addressStreet.error =null
                addressStreet.isErrorEnabled =false
                addressStreet.helperText=null
            }
        }

        if(area!=null){
            if(area.editText?.text.toString()==""){
                area.isErrorEnabled =true
                area.error ="Please Enter your Address"
                returnValue=returnValue and false
            }else{
                area.error =null
                area.isErrorEnabled =false
            }
        }
        return returnValue
    }

    override fun onDestroy() {
        println("Destroy called")
        deliveryAddressViewModel.apply {
            name=nameInputLayout.editText?.text.toString()
            phone=phoneInputLayout.editText?.text.toString()
            pinCode = if(pinCodeInputLayout.editText?.text.toString()!=""){
                pinCodeInputLayout.editText?.text.toString().toInt()
            }else{
                null
            }
            state=stateInputLayout.editText?.text.toString()
            city=cityInputLayout.editText?.text.toString()
            street=addressStreetInputLayout.editText?.text.toString()
            area=areaInputLayout.editText?.text.toString()
        }
        println("Saving ${deliveryAddressViewModel.city} ${deliveryAddressViewModel.pinCode}")
        super.onDestroy()
    }

}