package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment

class PaymentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Payment"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val paymentOptionRadioGroup=view.findViewById<RadioGroup>(R.id.payment_options)
        val placeOrderBtn=view.findViewById<Button>(R.id.place_order_btn)
        val payButton=view.findViewById<Button>(R.id.pay)
        paymentOptionRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            payButton.isEnabled=true
            when(checkedId){
                R.id.upi_payment->{
                    placeOrderBtn.visibility=View.GONE
                    payButton.visibility=View.VISIBLE
                }
                R.id.card_payment->{
                    payButton.visibility=View.VISIBLE
                    placeOrderBtn.visibility=View.GONE
                }
                R.id.cod->{
                    payButton.visibility=View.GONE
                    placeOrderBtn.visibility=View.VISIBLE
                }
            }
        }

        placeOrderBtn.setOnClickListener {
            replaceFragment(OrderPlacedFragment())
        }

        payButton.setOnClickListener {
            val upiBtn=view.findViewById<RadioButton>(R.id.upi_payment)
            val cardBtn=view.findViewById<RadioButton>(R.id.card_payment)

            when{
                upiBtn.isChecked->{
                    replaceFragment(UpiFragment())
                }
                cardBtn.isChecked->{
                    replaceFragment(CardPaymentFragment())
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.checkout_fragment_container, fragment)
        }
    }

}