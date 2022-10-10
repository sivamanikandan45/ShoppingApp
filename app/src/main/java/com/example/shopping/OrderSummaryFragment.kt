package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.CheckoutViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat

class OrderSummaryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: SelectedProductListAdapter
    private val cartViewModel: CartViewModel by activityViewModels()

private val checkoutViewModel:CheckoutViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Order Summary"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val customerNameTextView:TextView=view.findViewById(R.id.order_summary_customer_name)
        val customerAddressTextView:TextView=view.findViewById(R.id.order_summary_address)
        val customerPhnTextView:TextView=view.findViewById(R.id.order_summary_phn)

        val address=checkoutViewModel.selectedAddress.value
        var addressText=""

        if(address!=null){
            addressText="${address.street}, ${address.area}, ${address.city}, ${address.state} - ${address.pinCode}"
            customerNameTextView.text=address.name
            customerPhnTextView.text=address.phone
            customerAddressTextView.text=addressText
        }

        val totalAmountTextView=view.findViewById<TextView>(R.id.summary_total_amount_value)
        val finalTotalAmountTextView=view.findViewById<TextView>(R.id.summary_final_total_amt)
        val totalAmountBeforeDiscount=view.findViewById<TextView>(R.id.summary_original_price)
        val offerTextView=view.findViewById<TextView>(R.id.summary_discount_amount)
        val savingInfo=view.findViewById<TextView>(R.id.summary_saving_info)

        recyclerView=view.findViewById(R.id.selected_product_list)
        adapter= SelectedProductListAdapter()
        manager= LinearLayoutManager(context)

        GlobalScope.launch {
            val job=launch {
                adapter.setData(cartViewModel.getCartItems())
                val priceAfterDiscount=cartViewModel.getCartAmountAfterDiscount()
                val priceBeforeDiscount=cartViewModel.getCartAmountBeforeDiscount()
                withContext(Dispatchers.Main){
                    val decimalFormat = DecimalFormat("#.##")
                    decimalFormat.roundingMode = RoundingMode.UP
                    val priceAfterDiscountRounded= decimalFormat.format(priceAfterDiscount).toDouble()
                    totalAmountTextView.text="$"+priceAfterDiscountRounded.toString()
                    finalTotalAmountTextView.text="$"+priceAfterDiscountRounded.toString()
                    totalAmountBeforeDiscount.text=priceBeforeDiscount.toString()
                    var discountAmount=priceBeforeDiscount-priceAfterDiscount
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.UP
                    discountAmount = decimalFormat.format(discountAmount).toDouble()
                    offerTextView.text="-$"+discountAmount.toString()
                    savingInfo.text="You will save $$discountAmount on this order"
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                /*val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL) }
                divider?.dividerInsetStart=375
                recyclerView.addItemDecoration(divider!!)*/
                //totalAmountTextView.text="$"+cartViewModel.cartAmount.value.toString()
                recyclerView.adapter=adapter
                recyclerView.layoutManager=manager
            }

        }

        val continueButton=view.findViewById<Button>(R.id.continue_btn)
        continueButton.setOnClickListener {
            parentFragmentManager.commit{
                addToBackStack(null)
                replace(R.id.checkout_fragment_container,PaymentFragment())
            }
        }



        val changeAddressBtn=view.findViewById<Button>(R.id.change_address)
        changeAddressBtn.setOnClickListener {
            activity?.onBackPressed()
        }

    }
}