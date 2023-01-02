package com.example.shopping.payment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.shopping.checkout.ui.OrderPlacedFragment
import com.example.shopping.R
import com.example.shopping.payment.ui.viewmodel.UPIViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Matcher
import java.util.regex.Pattern


class UpiFragment : Fragment() {
    private val upiViewModel: UPIViewModel by viewModels()
    private lateinit var idInputLayout: TextInputLayout
    private lateinit var pinInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        idInputLayout=view.findViewById<TextInputLayout>(R.id.upi_id)
        pinInputLayout=view.findViewById<TextInputLayout>(R.id.upi_pin)

        if(upiViewModel.idEntered!=""){
            idInputLayout.editText?.setText(upiViewModel.idEntered)
        }
        if(upiViewModel.pinEntered!=""){
            pinInputLayout.editText?.setText(upiViewModel.pinEntered)
        }

        val payBtn=view.findViewById<Button>(R.id.pay)
        payBtn.setOnClickListener {
            if(validateUPIId()){
                parentFragmentManager.commit {
                    addToBackStack(null)
                    replace(R.id.checkout_fragment_container, OrderPlacedFragment())
                }
            }
        }

    }

    private fun validateUPIId(): Boolean {
        var returnValue=true
        val p: Pattern = Pattern.compile("[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}") //. represents single character
        val upiId=view?.findViewById<TextInputLayout>(R.id.upi_id)
        val id=upiId?.editText?.text.toString()

        val upiPin=view?.findViewById<TextInputLayout>(R.id.upi_pin)
        val pin=upiPin?.editText?.text.toString()

        val m: Matcher = p.matcher(id)

        if(upiId!=null){
            if(id==""){
                upiId.isErrorEnabled =true
                upiId.error ="Please Enter UPI ID"
                returnValue=returnValue and false
            }else if(!m.matches()){
                upiId.isErrorEnabled =true
                upiId.error ="Invalid UPI ID"
                returnValue=returnValue and false
            }else{
                upiId.error =null
                upiId.isErrorEnabled =false
            }
        }

        if(upiPin!=null){
            if(pin==""){
                upiPin.isErrorEnabled =true
                upiPin.error ="Please Enter UPI PIN"
                returnValue=returnValue and false
            }else if(pin.length==6||pin.length==4){
                upiPin.error =null
                upiPin.isErrorEnabled =false
            }
            else{
                upiPin.isErrorEnabled =true
                upiPin.error ="Invalid UPI PIN"
                returnValue=returnValue and false
            }
        }

        /*if(pin!=""&&id!=""){
            if(m.matches() && (String.valueOf(pin).length == 6 || String.valueOf(pin).length == 4)){
                return true
            }
        }*/
        return returnValue
        //return m.matches() && (String.valueOf(pin).length == 6 || String.valueOf(pin).length == 4)
    }

    override fun onDestroy() {
        upiViewModel.apply {
            idEntered=idInputLayout.editText?.text.toString()
            pinEntered=pinInputLayout.editText?.text.toString()
        }
        super.onDestroy()
    }


}