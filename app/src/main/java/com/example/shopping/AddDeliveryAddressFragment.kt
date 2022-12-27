package com.example.shopping

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.shopping.enums.FormMode
import com.example.shopping.model.Address
import com.example.shopping.viewmodel.AddressViewModel
import com.example.shopping.viewmodel.DeliveryAddressViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialContainerTransform
import java.util.regex.Pattern

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

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title="Add Delivery Address"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        /*(activity as AppCompatActivity).supportActionBar?.setShowHideAnimationEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.hide()*/
    }

    override fun onStop() {
        super.onStop()
        //(activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addressForm=view.findViewById<ConstraintLayout>(R.id.address_form)
        when(addressViewModel.formMode){
            FormMode.CREATE->{
                addressForm.transitionName="fab_transition"
            }
            FormMode.EDIT->{
                addressForm.transitionName="address_transition_${addressViewModel.selectedAddress.value?.addressId}"
            }
        }
        /*val menuHost:MenuHost=requireActivity()
        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    android.R.id.home->{
                        Toast.makeText(context,"Backpressesd",Toast.LENGTH_SHORT).show()
                        println("Back preseed")
                        return true
                    }
                }
                return false
            }

        })*/
        println("Fetched ${deliveryAddressViewModel.city} ${deliveryAddressViewModel.pinCode}")
        /*val toolbar=view.findViewById<Toolbar>(R.id.toolbar)
        //toolbar.inflateMenu(R.menu.product_fragment_menu)
        toolbar.title="Add Delivery Address"
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener { view ->
            activity?.onBackPressed()
        }*/
        val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharePreferences?.getInt("userId",-1)
        if (currentUserId != null) {
            addressViewModel.setUserId(currentUserId)
        }
        nameInputLayout=view.findViewById(R.id.fullname)
        phoneInputLayout=view.findViewById(R.id.phn)
        pinCodeInputLayout=view.findViewById(R.id.pincode)
        stateInputLayout=view.findViewById(R.id.state)
        cityInputLayout=view.findViewById(R.id.city)
        addressStreetInputLayout=view.findViewById(R.id.addressline1)
        areaInputLayout=view.findViewById(R.id.addressline2)

        if(addressViewModel.formMode==FormMode.EDIT){
            (activity as AppCompatActivity).supportActionBar?.apply {
                title="Edit Delivery Address"
                setDisplayHomeAsUpEnabled(true)
            }
            val selectedAddress=addressViewModel.selectedAddress.value
            if(selectedAddress!=null){
                nameInputLayout.editText?.setText(selectedAddress?.name!!)
                phoneInputLayout.editText?.setText(selectedAddress?.phone)
                selectedAddress?.pinCode?.let { pinCodeInputLayout.editText?.setText(it.toString()) }
                stateInputLayout.editText?.setText(selectedAddress?.state)
                cityInputLayout.editText?.setText(selectedAddress?.city)
                addressStreetInputLayout.editText?.setText(selectedAddress?.street)
                areaInputLayout.editText?.setText(selectedAddress?.area)

                if(!deliveryAddressViewModel.isModified){
                    deliveryAddressViewModel.apply {
                        name= selectedAddress.name
                        phone=selectedAddress.phone
                        pinCode=selectedAddress.pinCode
                        state=selectedAddress.state
                        city=selectedAddress.city
                        street=selectedAddress.street
                        area=selectedAddress.area
                    }
                }
            }


            if(deliveryAddressViewModel.isModified){
                nameInputLayout.editText?.setText(deliveryAddressViewModel.name)
                phoneInputLayout.editText?.setText(deliveryAddressViewModel.phone)
                deliveryAddressViewModel.pinCode?.let { pinCodeInputLayout.editText?.setText(it.toString()) }
                stateInputLayout.editText?.setText(deliveryAddressViewModel.state)
                cityInputLayout.editText?.setText(deliveryAddressViewModel.city)
                addressStreetInputLayout.editText?.setText(deliveryAddressViewModel.street)
                areaInputLayout.editText?.setText(deliveryAddressViewModel.area)
            }

        }

        if(deliveryAddressViewModel.name!=""){
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
                //val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                //val currentUserId=sharePreferences?.getInt("userId",-1)
                if(currentUserId!=null&&currentUserId!=-1){
                    if(addressViewModel.formMode==FormMode.CREATE){
                        val address=Address(0,currentUserId,name,phone,pinCode,state,city,street, area)
                        saveAddress(address)
                    }
                    if(addressViewModel.formMode==FormMode.EDIT){
                        val id=addressViewModel.selectedAddress.value?.addressId!!
                        val address=Address(id,currentUserId,name,phone,pinCode,state,city,street, area)
                        updateAddress(address)
                    }
                }
            }else{
                println("Some input error")
            }
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            init {
                println("back press is initialised")
            }
                override fun handleOnBackPressed() {
                    checkOnBackPressed()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    fun checkOnBackPressed() {
        if (addressViewModel.formMode == FormMode.CREATE) {
            if (hasContentInFields()) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Address will not be saved")
                    .setMessage("Are you sure you want to continue without saving?")
                    .setPositiveButton("CONTINUE") { _, _ ->
                        //requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,normalCallback )
                        parentFragmentManager.popBackStack()
                        //activity?.onBackPressed()
                    }
                    .setNegativeButton("CANCEL") { _, _ ->
                    }
                    .show()
            } else {
                parentFragmentManager.popBackStack()
            }
        } else {
            isFormModified()
            if (deliveryAddressViewModel.isModified) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Changes will not be saved")
                    .setMessage("Are you sure you want to continue without saving?")
                    .setPositiveButton("CONTINUE") { _, _ ->
                        //requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,normalCallback )
                        parentFragmentManager.popBackStack()
                        //activity?.onBackPressed()
                    }
                    .setNegativeButton("CANCEL") { _, _ ->
                    }
                    .show()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun hasContentInFields(): Boolean {
        val name=nameInputLayout.editText?.text.toString()
        val phone=phoneInputLayout.editText?.text.toString()
        val pinCode=pinCodeInputLayout.editText?.text.toString()
        val state=stateInputLayout.editText?.text.toString()
        val city=cityInputLayout.editText?.text.toString()
        val street=addressStreetInputLayout.editText?.text.toString()
        val area=areaInputLayout.editText?.text.toString()
        println("name is $name")
        return  name!=""||phone!=""||pinCode!=""||state!=""||city!=""||street!=""||area!=""
    }

    private fun saveAddress(address: Address) {
        addressViewModel.addAddress(address)
        parentFragmentManager.popBackStack()
    }

    private fun updateAddress(address: Address) {
        addressViewModel.updateAddress(address)
        parentFragmentManager.popBackStack()
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

        val indianPhoneNumberPattern=Pattern.compile("^[6-9][0-9]{9}$")
        val phnNumberMatcher=indianPhoneNumberPattern.matcher(phone?.editText?.text.toString())

        if(phone!=null){
            if(phone.editText?.text.toString()==""){
                phone.isErrorEnabled =true
                phone.error ="Please Enter your Phone Number"
                returnValue=returnValue and false
            }else if(!phnNumberMatcher.matches()){
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
        val houseNoPattern=Pattern.compile("^[1-9]\\d*(\\s*[-/]\\s*[1-9]\\d*)?(\\s?[a-zA-Z])?")
        val matcher=houseNoPattern.matcher(addressStreet?.editText?.text.toString())
        if(addressStreet!=null){
            if(addressStreet.editText?.text.toString()==""){
                addressStreet.isErrorEnabled =true
                addressStreet.error ="Please Enter your Address"
                returnValue=returnValue and false
            }else if(!matcher.find()){
                addressStreet.isErrorEnabled =true
                addressStreet.error ="Please Provide House number"
                returnValue=returnValue and false
            }
            else if(!addressStreet.editText?.text.toString().contains(",")){
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
        if(addressViewModel.formMode==FormMode.EDIT){
            isFormModified()
        }

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

    private fun isFormModified() {
        checkIsModified(nameInputLayout.editText?.text.toString(), deliveryAddressViewModel.name)
        checkIsModified(phoneInputLayout.editText?.text.toString(), deliveryAddressViewModel.phone)
        checkIsModified(
            pinCodeInputLayout.editText?.text.toString(),
            deliveryAddressViewModel.pinCode.toString()
        )
        checkIsModified(stateInputLayout.editText?.text.toString(), deliveryAddressViewModel.state)
        checkIsModified(cityInputLayout.editText?.text.toString(), deliveryAddressViewModel.city)
        checkIsModified(
            addressStreetInputLayout.editText?.text.toString(),
            deliveryAddressViewModel.street
        )
        checkIsModified(areaInputLayout.editText?.text.toString(), deliveryAddressViewModel.area)
    }

    private fun checkIsModified(editTextData:String,storedString: String) {
        if (editTextData != storedString) {
            println("$editTextData != $storedString")
            deliveryAddressViewModel.isModified = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        sharedElementEnterTransition=MaterialContainerTransform()
//        exitTransition = MaterialElevationScale(false)
        /*sharedElementEnterTransition=MaterialContainerTransform()
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale( true)*/
        super.onCreate(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                Toast.makeText(context,"Back pressed",Toast.LENGTH_SHORT).show()
                println("Back pressed")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}