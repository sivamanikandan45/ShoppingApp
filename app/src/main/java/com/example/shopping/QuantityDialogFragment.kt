package com.example.shopping

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.shopping.enums.CheckoutMode
import com.example.shopping.model.SelectedProductEntity
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.SelectQuantityViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuantityDialogFragment(private val parent:Fragment) :DialogFragment() {
    private val productViewModel: ProductViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val selectQuantityViewModel: SelectQuantityViewModel by activityViewModels()
    private lateinit var quantityDropDown:AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_quantity, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        /*if(savedInstanceState==null){
            return super.onCreateDialog(savedInstanceState)
        }else{
            return context?.let { Dialog(it) }!!
        }*/
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //retainInstance=true
    }

    /*override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }*/

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun onResume() {
        super.onResume()
        quantityDropDown=view?.findViewById<AutoCompleteTextView>(R.id.movie_dropdown)!!
        val list= mutableListOf<String>()
        for(i in 1 until 11){
            list.add(i.toString())
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, list)
        quantityDropDown?.setAdapter(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product=productViewModel.selectedProduct.value
        val quantityLayout=view?.findViewById<TextInputLayout>(R.id.quantity_layout)


        val cancelBtn=view.findViewById<Button>(R.id.cancel)
        cancelBtn.setOnClickListener {
            dismiss()
        }
        cartViewModel.cartItems.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(selectQuantityViewModel.checkoutMode==CheckoutMode.OVERALL){
                val submitBtn=view.findViewById<Button>(R.id.add)
                submitBtn.text="Add"
                if(product?.let { cartViewModel.isProductInCart(it.productId) } == true){
                    val previousCount=cartViewModel.getProductCount(product.productId)
                    quantityDropDown=view?.findViewById<AutoCompleteTextView>(R.id.movie_dropdown)
                    quantityDropDown.setText(previousCount.toString(),false)
                    quantityLayout.helperText="Already $previousCount available in the cart"
                }
                else{
                    println("Product is not available")
                    quantityDropDown=view?.findViewById<AutoCompleteTextView>(R.id.movie_dropdown)
                    quantityDropDown.setText("1",false)
                }
            }else{
                val submitBtn=view.findViewById<Button>(R.id.add)
                submitBtn.text="Buy Now"

            }

        })

        val submitBtn=view.findViewById<Button>(R.id.add)
        submitBtn.setOnClickListener {
            if(selectQuantityViewModel.checkoutMode==CheckoutMode.OVERALL){
                val count=quantityDropDown.text.toString().toInt()
                if(product!=null){
                    val oldPriceForSelectedQty=count*product.originalPrice
                    val priceForSelectedQty=count*product.priceAfterDiscount
                    val selectedProductEntity= SelectedProductEntity(product.productId,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,count,oldPriceForSelectedQty,priceForSelectedQty)
                    cartViewModel.addToCart(selectedProductEntity)
                    Snackbar.make(parent.requireView(),"Added to the Cart",Snackbar.LENGTH_LONG)
                            .setAnchorView(parent.requireView().findViewById(R.id.button_layout))
                            .show()
                    dismiss()
                }else{
                    println("product is null")
                }
            }else{
                if(validateInput()){
                    val intent=Intent(requireContext(),CheckoutActivity::class.java)
                    intent.putExtra("checkoutMode","buy_now")
                    intent.putExtra("productId",product?.productId)
                    intent.putExtra("quantity",quantityDropDown.text.toString().toInt())
                    startActivity(intent)
                    dismiss()
                }
            }

        }
    }

    private fun validateInput(): Boolean {
        var returnValue=true
        val quantityLayout=view?.findViewById<TextInputLayout>(R.id.quantity_layout)
        val quantityDropDown=view?.findViewById<AutoCompleteTextView>(R.id.movie_dropdown)
        val quantity=quantityDropDown?.text.toString()
        if(quantityLayout!=null&&quantityDropDown!=null){
            if(quantity==""){
                quantityLayout.isErrorEnabled =true
                quantityLayout.error ="Please Select Quantity"
                returnValue=returnValue and false
            }
            else{
                quantityLayout.error =null
                quantityLayout.isErrorEnabled =false
            }
        }
    return returnValue
    }

    private fun gotoCart() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("fragment", "cart")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}