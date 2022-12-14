package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputLayout


class CardPaymentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val monthDropDownTextField=view.findViewById<AutoCompleteTextView>(R.id.month_dropdown)
        val monthList= listOf("01 - Jan","02 - Feb","03 - Mar","04 - Apr","05 - May","06 - Jun","07 - Jul","08 - Aug","09 - Sep","10 - Oct","11 - Nov","12 - Dec")
        val monthDropDownAdapter = ArrayAdapter(requireContext(), R.layout.list_item, monthList)
        monthDropDownTextField.setAdapter(monthDropDownAdapter)


        val yearDropDownTextField=view.findViewById<AutoCompleteTextView>(R.id.year_dropdown)
        var yearList= mutableListOf<String>()
        for(i in 0 until 50){
            yearList.add(i,(i+22).toString())
        }
        val yearDropDownAdapter = ArrayAdapter(requireContext(), R.layout.list_item, yearList)
        yearDropDownTextField.setAdapter(yearDropDownAdapter)


        val payBtn=view.findViewById<Button>(R.id.pay)
        payBtn.setOnClickListener {
            if(validateUPIId()){
                parentFragmentManager.commit {
                    addToBackStack(null)
                    replace(R.id.checkout_fragment_container, OrderPlacedFragment())
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                }
            }
        }

    }

    private fun validateUPIId(): Boolean {
        var returnValue=true

        val cardNoLayout=view?.findViewById<TextInputLayout>(R.id.card_no)
        val cardNo=cardNoLayout?.editText?.text.toString()

        val cvv=view?.findViewById<TextInputLayout>(R.id.cvv)
        val cvvValue=cvv?.editText?.text.toString()

        val monthLayout=view?.findViewById<TextInputLayout>(R.id.month)
        val monthTextField=view?.findViewById<AutoCompleteTextView>(R.id.month_dropdown)
        val month=monthTextField?.text.toString()

        if(monthTextField!=null&&monthLayout!=null){
            if(month==""){
                monthLayout.isErrorEnabled =true
                monthLayout.error ="Please Select Month"
                returnValue=returnValue and false
            }
            else{
                monthLayout.error =null
                monthLayout.isErrorEnabled =false
            }
        }

        val yearLayout=view?.findViewById<TextInputLayout>(R.id.year)
        val yearTextField=view?.findViewById<AutoCompleteTextView>(R.id.year_dropdown)
        val year=yearTextField?.text.toString()

        if(yearTextField!=null&&yearLayout!=null){
            if(year==""){
                yearLayout.isErrorEnabled =true
                yearLayout.error ="Please Select Month"
                returnValue=returnValue and false
            }
            else{
                yearLayout.error =null
                yearLayout.isErrorEnabled =false
            }
        }


        if(cardNoLayout!=null){
            if(cardNo==""){
                cardNoLayout.isErrorEnabled =true
                cardNoLayout.error ="Please Enter Card Number"
                returnValue=returnValue and false
            }else if(cardNo.length<16){
                cardNoLayout.isErrorEnabled =true
                cardNoLayout.error ="Invalid Card No"
                returnValue=returnValue and false
            }else{
                cardNoLayout.error =null
                cardNoLayout.isErrorEnabled =false
            }
        }


        if(cvv!=null){
            if(cvvValue==""){
                cvv.isErrorEnabled =true
                cvv.error ="Please Enter CVVr"
                returnValue=returnValue and false
            }else if(cvvValue.length<3){
                cvv.isErrorEnabled =true
                cvv.error ="Invalid CVV"
                returnValue=returnValue and false
            }else{
                cvv.error =null
                cvv.isErrorEnabled =false
            }
        }

        return returnValue
    }

}