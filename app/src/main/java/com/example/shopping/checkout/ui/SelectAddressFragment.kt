package com.example.shopping.checkout.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.util.ItemClickListener
import com.example.shopping.R
import com.example.shopping.address.ui.AddDeliveryAddressFragment
import com.example.shopping.checkout.ui.adapter.SelectAddressListAdapter
import com.example.shopping.address.ui.viewmodel.AddressViewModel
import com.example.shopping.checkout.ui.viewmodel.CheckoutViewModel

class SelectAddressFragment : Fragment() {
    private lateinit var addressListRecyclerView:RecyclerView
    private lateinit var addressListManager: LinearLayoutManager
    private lateinit var selectAddressListAdapter: SelectAddressListAdapter
    private val addressViewModel: AddressViewModel by activityViewModels()
    private val checkoutViewModel: CheckoutViewModel by activityViewModels()

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
        val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharePreferences?.getInt("userId",-1)
        if (currentUserId != null) {
            addressViewModel.setUserId(currentUserId)
        }
        val deliverHereBtn=view.findViewById<Button>(R.id.deliver_here)

        addressListRecyclerView=view.findViewById(R.id.address_list_recycler_view)
        selectAddressListAdapter= SelectAddressListAdapter()
        selectAddressListAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                //selectAddressListAdapter.selectedPosition=position
                selectAddressListAdapter.updateSelectedPosition(position)
                checkoutViewModel.selectedAddressPosition=selectAddressListAdapter.selectedPosition
                //println("Item clicked at $position")
            }
        })

        //selectAddressListAdapter.setData(addressViewModel.getAddressLists())
        addressViewModel.getAddressLists()
        //println)
        addressListManager= LinearLayoutManager(requireContext())
        addressListRecyclerView.adapter=selectAddressListAdapter
        addressListRecyclerView.layoutManager=addressListManager

        addressViewModel.addressList.observe(viewLifecycleOwner, Observer {
            println("observed ${it.size }")
            selectAddressListAdapter.setData(it)
            if(it.isEmpty()){
                println("address list is empty")
                deliverHereBtn.isEnabled=false
            }else{
                deliverHereBtn.isEnabled=true
            }
            selectAddressListAdapter.updateSelectedPosition(checkoutViewModel.selectedAddressPosition)
            //selectAddressListAdapter.notifyDataSetChanged()
        })


        deliverHereBtn.setOnClickListener {
            checkoutViewModel.selectedAddress.value=addressViewModel.addressList.value?.get(selectAddressListAdapter.selectedPosition)
            println("Selected address is ${checkoutViewModel.selectedAddress.value}")
            checkoutViewModel.selectedAddressPosition=selectAddressListAdapter.selectedPosition
            parentFragmentManager.commit{
                //addToBackStack(null)
                replace(R.id.checkout_fragment_container, OrderSummaryFragment())
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }
        }







        //createRadioButtons()




        val addNewAddressBtn=view.findViewById<CardView>(R.id.add_new_address)
        addNewAddressBtn.setOnClickListener {
            parentFragmentManager.commit{
                addToBackStack(null)
                replace(R.id.checkout_fragment_container, AddDeliveryAddressFragment())
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }
        }

    }

    /*private fun createRadioButtons() {
        val group=view?.findViewById<RadioGroup>(R.id.address_radio_group)
        if(group!=null){
            val array= arrayOf("Apple","Ball","Cat")
            for(i in array.indices){
                val address=array[i]
                val radioButton=RadioButton(context)
                radioButton.text=address
                radioButton.textSize=14F
                radioButton.updatePadding(50)
               // radioButton.MarginLayoutParams.updateMargins()
                group.addView(radioButton)
            }
        }
    }*/
}