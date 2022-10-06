package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.updatePadding
import androidx.fragment.app.commit

class SelectAddressFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Select Address"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        createRadioButtons()




        val addNewAddressBtn=view.findViewById<CardView>(R.id.add_new_address)
        addNewAddressBtn.setOnClickListener {
            parentFragmentManager.commit{
                addToBackStack(null)
                replace(R.id.checkout_fragment_container,AddDeliveryAddressFragment())
            }
        }

    }

    private fun createRadioButtons() {
        val group=view?.findViewById<RadioGroup>(R.id.address_radio_group)
        if(group!=null){
            val array= arrayOf("Apple","Ball","Cat")
            for(i in array.indices){
                val address=array[i]
                val radioButton=RadioButton(context)
                radioButton.text=address
                radioButton.textSize=14F
                radioButton.updatePadding(16)
                group.addView(radioButton)
            }
        }
    }
}