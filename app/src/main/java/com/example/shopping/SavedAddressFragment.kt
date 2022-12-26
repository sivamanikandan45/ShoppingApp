package com.example.shopping

import android.content.Context
import android.graphics.Color
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.enums.FormMode
import com.example.shopping.viewmodel.AddressViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale

class SavedAddressFragment : Fragment() {
    private val addressViewModel: AddressViewModel by activityViewModels()
    private lateinit var addressListRecyclerView: RecyclerView
    private lateinit var addressListManager: LinearLayoutManager
    private lateinit var savedAddressListAdapter: SavedAddressListAdapter
    private lateinit var addNewAddressBtn:FloatingActionButton
    private lateinit var fabBg:View
    //private lateinit var parent:ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        val activity=(activity as AppCompatActivity)
        val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharePreferences?.getInt("userId",-1)
        println("Saved user1 id is:$currentUserId")
        if (currentUserId != null) {
            println("Saved user id is:$currentUserId")
            addressViewModel.setUserId(currentUserId)
        }
        activity.supportActionBar?.title="Saved Address"
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val emptyPage=view.findViewById<ConstraintLayout>(R.id.empty_address)
        addNewAddressBtn=view.findViewById<FloatingActionButton>(R.id.add_new_address)
        fabBg=view.findViewById<View>(R.id.bg_fab)
        //parent=view.findViewById<ConstraintLayout>(R.id.root)

        addressListRecyclerView=view.findViewById(R.id.address_list_recycler_view)
        savedAddressListAdapter= SavedAddressListAdapter()
        savedAddressListAdapter.setAddressMenuListener(object : AddressMenuListener {
            override fun deleteAddress(position: Int) {
                println("Delete address at $position")
                addressViewModel.addressList.value?.get(position)?.addressId?.let {
                    addressViewModel.deleteAddress(it)
                }
                savedAddressListAdapter.notifyDataSetChanged()
            }

            override fun editAddress(position: Int) {
                addressViewModel.formMode=FormMode.EDIT
                addressViewModel.selectedAddress.value=addressViewModel.addressList.value?.get(position)
                replaceFragment(AddDeliveryAddressFragment(),position)
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
        addressListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy>0){
                    addNewAddressBtn.hide()
                }
                else{
                    addNewAddressBtn.show()
                }
            }
        })
        val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL) }
        divider?.isLastItemDecorated=false
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

        /*val animation=AnimationUtils.loadAnimation(requireContext(),R.anim.fab_anim).apply {
            duration=700
            interpolator=AccelerateDecelerateInterpolator()
        }*/


        addNewAddressBtn.setOnClickListener {
            addressViewModel.formMode=FormMode.CREATE
            replaceFragment(AddDeliveryAddressFragment(), 0)
        }

    }

    private fun replaceFragment(fragment: Fragment, position: Int) {
        parentFragmentManager.commit {
            addToBackStack(null)
            /*fabBg.visibility=View.VISIBLE
            addSharedElement(fabBg,"fab_transition")
            fragment.sharedElementEnterTransition=MaterialContainerTransform().apply {
                startView=addNewAddressBtn
                endView=fragment.view?.findViewById(R.id.address_from)
                scrimColor = Color.TRANSPARENT
                setPathMotion(MaterialArcMotion())
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }*/
            fabBg.visibility=View.VISIBLE
            //addSharedElement(fabBg,"fab_transition")
            when(addressViewModel.formMode){
                FormMode.CREATE->{
                    addSharedElement(fabBg,"fab_transition")
                    fragment.sharedElementEnterTransition=MaterialContainerTransform().apply {
                        scrimColor=Color.TRANSPARENT
                        duration=700L
                        interpolator=AccelerateDecelerateInterpolator()
                        setPathMotion(MaterialArcMotion())
                        containerColor=Color.parseColor("#ffffff")
                        startContainerColor = Color.parseColor("#ffffff")
                        endContainerColor = Color.parseColor("#ffffff")
                    }
                }
                FormMode.EDIT->{
                    val item=addressListRecyclerView.findViewHolderForAdapterPosition(position)?.itemView
                    val address=addressViewModel.addressList.value?.get(position)
                    if (address != null) {
                        item?.transitionName="address_transition_${address.addressId}"
                        addSharedElement(item!!,"address_transition_${address.addressId}")
                    }
                    fragment.sharedElementEnterTransition=MaterialContainerTransform().apply {
                        duration=250L
//                        interpolator=AccelerateDecelerateInterpolator()
                        scrimColor=Color.TRANSPARENT
                    }
                }
            }


            replace(R.id.account_fragment_container, fragment)
            //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(/* growing= */ false)
        reenterTransition = MaterialElevationScale(/* growing= */ true)
    }
}