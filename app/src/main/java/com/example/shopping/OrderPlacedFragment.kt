package com.example.shopping

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.shopping.enums.CheckoutMode
import com.example.shopping.model.Order
import com.example.shopping.model.OrderedProduct
import com.example.shopping.viewmodel.AddressViewModel
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.CheckoutViewModel
import com.example.shopping.viewmodel.OrderViewModel
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderPlacedFragment : Fragment() {

    private val checkoutViewModel:CheckoutViewModel by activityViewModels()
    private val cartViewModel:CartViewModel by activityViewModels()
    private val orderViewModel:OrderViewModel by activityViewModels()
    private val addressViewModel:AddressViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_placed, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Order Status"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        /*when(checkoutViewModel.mode){
            CheckoutMode.OVERALL->{placeOrder()}
            CheckoutMode.BUY_NOW->{buyNow()}
        }*/
        val redirect=view.findViewById<TextView>(R.id.redirect)
        when(checkoutViewModel.mode){
            CheckoutMode.BUY_NOW->{
                redirect.text="Redirecting to Orders...."
            }
            else->{
                redirect.text="Redirecting to Cart...."
            }
        }
        placeOrder()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    Log.d("BACKBUTTON", "Back button clicks")
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        /*requireActivity().onBackPressedDispatcher.addCallback(this){
            return OnBackPressedCallback(){
            }

        }*/
    }

    private fun buyNow() {
        TODO("Not yet implemented")
    }

    private fun placeOrder() {
        GlobalScope.launch {
            val job=launch(Dispatchers.IO) {
                val decimalFormat = DecimalFormat("#.##")
                decimalFormat.roundingMode = RoundingMode.UP
                val selectedAddressId=checkoutViewModel.selectedAddress.value?.addressId
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                val calendar: Calendar = Calendar.getInstance()
                calendar.time = Date() // Using today's date
                calendar.add(Calendar.DATE, 2) // Adding 2 days
                val deliveryDate = formatter.format(calendar.getTime())
                val date = Date()
                val ordDate = formatter.format(date)
                var priceBeforeDiscount: Double? =null
                var priceAfterDiscount: Double? =null
                //var discountAmount:Double?=null
                var itemCount:Int?=1
                if(checkoutViewModel.mode==CheckoutMode.OVERALL){
                    priceBeforeDiscount=cartViewModel.cartAmountBeforeDiscount.value
                    priceAfterDiscount=cartViewModel.cartAmountAfterDiscount.value
                    itemCount=cartViewModel.cartItems.value?.size
                }else if(checkoutViewModel.mode==CheckoutMode.BUY_NOW){
                    val selectedProduct=checkoutViewModel.buyNowProduct
                    priceBeforeDiscount=selectedProduct?.oldPriceForSelectedQuantity
                    priceAfterDiscount=selectedProduct?.priceForSelectedQuantity
                }

                var discountAmount = priceBeforeDiscount!! - priceAfterDiscount!!
                discountAmount = decimalFormat.format(discountAmount).toDouble()

                val address= selectedAddressId?.let { addressViewModel.getAddress(it) }
                var order: Order? =null
                if(address!=null) {
                     order= itemCount?.let {
                        Order(
                            0,
                            address.name,
                            address.phone,
                            address.pinCode,
                            address.state,
                            address.city,
                            address.street,
                            address.area,
                            it,
                            priceBeforeDiscount,
                            discountAmount,
                            priceAfterDiscount,
                            ordDate,
                            deliveryDate,
                            checkoutViewModel.paymentMode
                        )
                    }
                }
                println("Hello world")
                if(order!=null){
                    val rowId=orderViewModel.placeOrder(order)
                    println("Row iD is $rowId")
                    val id=orderViewModel.getIdUsingRowId(rowId)
                    println("Id of the $id")
                    if(checkoutViewModel.mode==CheckoutMode.OVERALL){
                        for(i in 0 until cartViewModel.cartItems.value?.size!!){
                            val prod= cartViewModel.cartItems.value!![i]
                            val orderedProduct=OrderedProduct(0,id,prod.productId,prod.productName,prod.productBrand,prod.oldPriceForSelectedQuantity,prod.priceForSelectedQuantity,prod.discount,prod.quantity,prod.imageUrl)
                            orderViewModel.addOrderedProduct(orderedProduct)
                        }
                        cartViewModel.clearCartItems()
                    }else if(checkoutViewModel.mode==CheckoutMode.BUY_NOW){
                        val prod=checkoutViewModel.buyNowProduct
                        if(prod!=null){
                            val orderedProduct=OrderedProduct(0,id,prod.productId,prod.productName,prod.productBrand,prod.oldPriceForSelectedQuantity,prod.priceForSelectedQuantity,prod.discount,prod.quantity,prod.imageUrl)
                            orderViewModel.addOrderedProduct(orderedProduct)
                        }
                        checkoutViewModel.buyNowProductId=0
                        checkoutViewModel.buyNowProductQuantity=0
                        checkoutViewModel.buyNowProduct=null
                    }

                }
                delay(1000)
            }
            job.join()
            withContext(Dispatchers.Main){
                val intent=Intent(requireContext(),MainActivity::class.java)
                if(checkoutViewModel.mode==CheckoutMode.BUY_NOW){
                    intent.putExtra("fragment","account")
                }
                else if(checkoutViewModel.mode==CheckoutMode.OVERALL){
                    intent.putExtra("fragment","cart")
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
            //job.join()
    }

}