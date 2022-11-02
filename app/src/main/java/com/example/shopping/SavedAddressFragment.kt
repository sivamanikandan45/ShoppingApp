package com.example.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.enums.FormMode
import com.example.shopping.viewmodel.AddressViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedAddressFragment : Fragment() {
    private val addressViewModel: AddressViewModel by activityViewModels()
    private lateinit var addressListRecyclerView: RecyclerView
    private lateinit var addressListManager: LinearLayoutManager
    private lateinit var savedAddressListAdapter: SavedAddressListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity=(activity as AppCompatActivity)
        activity.supportActionBar?.title="Saved Address"
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val emptyPage=view.findViewById<ConstraintLayout>(R.id.empty_address)

        addressListRecyclerView=view.findViewById(R.id.address_list_recycler_view)
        savedAddressListAdapter= SavedAddressListAdapter()
        savedAddressListAdapter.setAddressMenuListener(object : AddressMenuListener {
            override fun deleteAddress(position: Int) {
                println("Delete address at $position")
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) { addressViewModel.addressList.value?.get(position)?.addressId?.let {
                        addressViewModel.deleteAddress(it)
                    }
                    }
                    job.join()
                    withContext(Dispatchers.Main){
                        savedAddressListAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun editAddress(position: Int) {
                addressViewModel.formMode=FormMode.EDIT
                addressViewModel.selectedAddress.value=addressViewModel.addressList.value?.get(position)
                replaceFragment(AddDeliveryAddressFragment())
            }
        })

        val list=addressViewModel.getAddressLists()
        if(list.isEmpty()){
            emptyPage.visibility=View.VISIBLE
            addressListRecyclerView.visibility=View.GONE
        }

        savedAddressListAdapter.setData(list)
        println(addressViewModel.getAddressLists())
        addressListManager= LinearLayoutManager(requireContext())
        addressListRecyclerView.adapter=savedAddressListAdapter
        addressListRecyclerView.layoutManager=addressListManager
        val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL) }
        //divider?.dividerInsetStart=
        addressListRecyclerView.addItemDecoration(divider!!)

        addressViewModel.addressList.observe(viewLifecycleOwner, Observer {
            println("observed $it")
            if(it.isEmpty()){
                emptyPage.visibility=View.VISIBLE
                addressListRecyclerView.visibility=View.GONE
            }else{
                emptyPage.visibility=View.GONE
                addressListRecyclerView.visibility=View.VISIBLE
                savedAddressListAdapter.setData(it)
                savedAddressListAdapter.notifyDataSetChanged()
            }
        })

        val addNewAddressBtn=view.findViewById<CardView>(R.id.add_new_address)
        addNewAddressBtn.setOnClickListener {
            addressViewModel.formMode=FormMode.CREATE
            replaceFragment(AddDeliveryAddressFragment())
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.account_fragment_container, fragment)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }
}