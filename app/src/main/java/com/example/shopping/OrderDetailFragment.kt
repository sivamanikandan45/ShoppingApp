package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.OrderedProduct
import com.example.shopping.viewmodel.AddressViewModel
import com.example.shopping.viewmodel.OrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat

class OrderDetailFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: OrderedProductListAdapter
    private val orderViewModel:OrderViewModel by activityViewModels()
    private val addressViewModel:AddressViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println(orderViewModel.selectedOrder.value)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title="My Orders"
            setDisplayHomeAsUpEnabled(true)
        }
        val orderId=view.findViewById<TextView>(R.id.order_details_id)
        val orderedDate=view.findViewById<TextView>(R.id.order_date_detail)
        val billAmount=view.findViewById<TextView>(R.id.bill_amount)
        val modeOfPayment=view.findViewById<TextView>(R.id.mode_of_payment)
        val noOfItem=view.findViewById<TextView>(R.id.no_of_item_value)
        val expectedDeliveryDate=view.findViewById<TextView>(R.id.expected_delivery_date)
        val deliveryAddress=view.findViewById<TextView>(R.id.delivery_address)
        val originalBillAmount=view.findViewById<TextView>(R.id.order_original_price)
        val discountAmount=view.findViewById<TextView>(R.id.order_discount_amount)
        val totalBillAmount=view.findViewById<TextView>(R.id.order_total_amount_value)

        recyclerView=view.findViewById(R.id.ordered_product_list)
        adapter= OrderedProductListAdapter()
        manager= LinearLayoutManager(context)


        val selectedOrder=orderViewModel.selectedOrder.value
        var orderedProducts= listOf<OrderedProduct>()

        if(selectedOrder!=null){
            orderId.text="#${selectedOrder.orderId}"
            orderedDate.text=selectedOrder.orderedDate

            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.roundingMode = RoundingMode.UP
            val amountAfterDiscountRounded = decimalFormat.format(selectedOrder.totalAfterDiscount).toDouble()
            val withSymbol="₹$amountAfterDiscountRounded"
            billAmount.text=withSymbol

            modeOfPayment.text=selectedOrder.paymentMode
            noOfItem.text="${selectedOrder.itemCount} Item"
            expectedDeliveryDate.text=selectedOrder.expectedDeliveryDate
            originalBillAmount.text="₹${selectedOrder.originalTotalPrice}"
            discountAmount.text="-₹${selectedOrder.discount}"
            totalBillAmount.text=withSymbol
            deliveryAddress.text="${selectedOrder.customerName},\n${selectedOrder.street}, ${selectedOrder.area}, ${selectedOrder.city}, ${selectedOrder.state} - ${selectedOrder.pinCode}\n${selectedOrder.customerPhone}"

            GlobalScope.launch {
                val job=launch(Dispatchers.IO) {
                    orderedProducts=orderViewModel.getOrderedProduct(selectedOrder.orderId)
                    //val address=addressViewModel.getAddress(selectedOrder.addressId)
                    withContext(Dispatchers.Main){
                        adapter.setData(orderedProducts)
                        recyclerView.adapter=adapter
                        recyclerView.layoutManager=manager
                        /*val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL) }
                        divider?.isLastItemDecorated = false
                        divider?.let { recyclerView.addItemDecoration(it) }*/
                        /*if(address!=null){
                            deliveryAddress.text="${address.name},\n${address.street}, ${address.area}, ${address.city}, ${address.state} - ${address.pinCode}\n${address.phone}"
                        }else{
                            deliveryAddress.text="Not Found"
                        }*/

                    }
                    /*println(address)
                    println(orderedProducts)*/
                }
                job.join()
            }

        }






    }

}