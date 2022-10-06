package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.shopping.model.Address
import com.example.shopping.viewmodel.AddressViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDeliveryAddressFragment : Fragment() {
    private val addressViewModel:AddressViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_delivery_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Add Delivery Address"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val nameInputLayout=view.findViewById<TextInputLayout>(R.id.fullname)
        val phoneInputLayout=view.findViewById<TextInputLayout>(R.id.phn)
        val pinCodeInputLayout=view.findViewById<TextInputLayout>(R.id.pincode)
        val stateInputLayout=view.findViewById<TextInputLayout>(R.id.state)
        val cityInputLayout=view.findViewById<TextInputLayout>(R.id.city)
        val addressStreetInputLayout=view.findViewById<TextInputLayout>(R.id.addressline1)
        val areaInputLayout=view.findViewById<TextInputLayout>(R.id.addressline2)

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
                val address=Address(0,name,phone,pinCode,state,city,street, area)
                saveAddress(address)

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
            }else if(pinCode.editText?.text.toString().length<6){
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
            }else{
                addressStreet.error =null
                addressStreet.isErrorEnabled =false
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

}