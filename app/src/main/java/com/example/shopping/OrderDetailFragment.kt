package com.example.shopping

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.OrderedProduct
import com.example.shopping.model.OrderedProductEntity
import com.example.shopping.viewmodel.AddressViewModel
import com.example.shopping.viewmodel.OrderViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.Dispatchers
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
    private val productViewModel:ProductViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    /*override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.show()
        super.onResume()
    }*/

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden){
            (activity as AppCompatActivity).supportActionBar?.show()
        }
        //super.onHiddenChanged(hidden)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println(orderViewModel.selectedOrder.value)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title="My Orders"
            setDisplayHomeAsUpEnabled(true)
            if(orderViewModel.fromCheckOutPage){
                setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
                title="Order Detail"
                val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        startMainActivity()
                    }
                }
                if(requireActivity() is CheckoutActivity){
                    println("Back callback is set ")
                    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
                }
            }
        }

        val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharePreferences?.getInt("userId",-1)
        if (currentUserId != null) {
            orderViewModel.setUserId(currentUserId)
        }

        /*val nestedScrollView=view.findViewById<NestedScrollView>(R.id.root_order_detail)
        val reOrderFab=view.findViewById<ExtendedFloatingActionButton>(R.id.reorder_fab)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if(scrollY>0){
                    reOrderFab.shrink()
                }
                else{
                    reOrderFab.extend()
                }
            }
        }*/

        val orderId=view.findViewById<TextView>(R.id.order_id_label)
        val orderedDate=view.findViewById<TextView>(R.id.order_date_detail)
        val billAmount=view.findViewById<TextView>(R.id.bill_amount)
        val modeOfPayment=view.findViewById<TextView>(R.id.mode_of_payment)
        val noOfItem=view.findViewById<TextView>(R.id.no_of_item_value)
        val expectedDeliveryDate=view.findViewById<TextView>(R.id.expected_delivery_date)
        val deliveryAddress=view.findViewById<TextView>(R.id.delivery_address)
        val originalBillAmount=view.findViewById<TextView>(R.id.order_original_price)
        val discountAmount=view.findViewById<TextView>(R.id.order_discount_amount)
        val totalBillAmount=view.findViewById<TextView>(R.id.order_total_amount_value)
        val phnNumber=view.findViewById<TextView>(R.id.order_detail_phn)

        recyclerView=view.findViewById(R.id.ordered_product_list)
        adapter= OrderedProductListAdapter()
        manager= LinearLayoutManager(context)


        val selectedOrder=orderViewModel.selectedOrder.value
        var orderedProductEntities= listOf<OrderedProduct>()

        if(selectedOrder!=null){
            orderId.text="ORDER #${selectedOrder.orderId}"
            orderedDate.text=selectedOrder.orderedDate

            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.roundingMode = RoundingMode.UP
            val amountAfterDiscountRounded = decimalFormat.format(selectedOrder.totalAfterDiscount).toDouble()
            val withSymbol="₹$amountAfterDiscountRounded"
            billAmount.text=withSymbol

            modeOfPayment.text=selectedOrder.paymentMode
            noOfItem.text="(${selectedOrder.itemCount} Item)"
            expectedDeliveryDate.text=selectedOrder.expectedDeliveryDate
            originalBillAmount.text="₹${selectedOrder.originalTotalPrice}"
            discountAmount.text="-₹${selectedOrder.discount}"
            totalBillAmount.text=withSymbol
            deliveryAddress.text="${selectedOrder.customerName},\n${selectedOrder.street}, ${selectedOrder.area}, ${selectedOrder.city}, ${selectedOrder.state} - ${selectedOrder.pinCode}"
            phnNumber.text="${selectedOrder.customerPhone}"

            lifecycleScope.launch {
                val job=launch(Dispatchers.IO) {
                    orderedProductEntities=orderViewModel.getOrderedProduct(selectedOrder.orderId)
                    //val address=addressViewModel.getAddress(selectedOrder.addressId)
                    withContext(Dispatchers.Main){
                        adapter.setData(orderedProductEntities)
                        adapter.setOnItemClickListener(object :ItemClickListener{
                            override fun onItemClick(position: Int) {
                                parentFragmentManager.commit {
                                    val product=orderedProductEntities[position]
                                    productViewModel.selectedProduct.value=productViewModel.getProductFromID(product.productId)
                                    if (productViewModel.selectedProduct.value!=null){
                                        hide(this@OrderDetailFragment)
                                        val fragment=ProductFragment()
                                        val item=recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                                        item?.transitionName="cart_item_transition_${product.productId}"
                                        addSharedElement(item!!,"cart_item_transition_${product.productId}")
                                        fragment.sharedElementEnterTransition= MaterialContainerTransform().apply {
                                                        duration=250L
                                                        scrimColor= Color.TRANSPARENT
                                                    }
                                        if(requireActivity() is CheckoutActivity){
                                            add(R.id.checkout_fragment_container,fragment)
                                        }else if(requireActivity() is AccountActivity){
                                            add(R.id.account_fragment_container,fragment)
                                        }
                                        addToBackStack(null)
                                    }
                                }

                            }
                        })
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
                }
                job.join()
            }

        }






    }

    fun startMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}