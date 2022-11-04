package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.viewmodel.AddressViewModel
import com.example.shopping.viewmodel.OrderViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyOrdersFragment : Fragment() {

    private val orderViewModel: OrderViewModel by activityViewModels()
    private lateinit var orderListRecyclerView: RecyclerView
    private lateinit var orderListManager: LinearLayoutManager
    private lateinit var orderAdapter: OrderAdapter

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            val job=launch(Dispatchers.IO) {
                orderViewModel.getOrdersFromDB()
            }
            job.join()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity=(activity as AppCompatActivity)
        activity.supportActionBar?.title="My Orders"
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val emptyPage=view.findViewById<ConstraintLayout>(R.id.empty_order)

        orderListRecyclerView=view.findViewById(R.id.orders_recycler_view)
        orderAdapter= OrderAdapter()
        orderAdapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                orderViewModel.selectedOrder.value=orderViewModel.orderList.value?.get(position)
                replaceFragment(OrderDetailFragment())
            }
        })
        val list=orderViewModel.getOrderList()
        if(list.isEmpty()){
            emptyPage.visibility=View.VISIBLE
            orderListRecyclerView.visibility=View.GONE
        }
        orderAdapter.setData(list)
        orderListManager= LinearLayoutManager(requireContext())
        orderListRecyclerView.adapter=orderAdapter
        orderListRecyclerView.layoutManager=orderListManager
        val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL) }
        //divider?.dividerInsetStart=
        orderListRecyclerView.addItemDecoration(divider!!)

        orderViewModel.orderList.observe(viewLifecycleOwner, Observer {

            if(it.isEmpty()){
                emptyPage.visibility=View.VISIBLE
                orderListRecyclerView.visibility=View.GONE
            }else{
                emptyPage.visibility=View.GONE
                orderListRecyclerView.visibility=View.VISIBLE
                orderAdapter.setData(it)
                orderAdapter.notifyDataSetChanged()
            }
            /*println("observed $it")
            orderAdapter.setData(it)
            orderAdapter.notifyDataSetChanged()*/
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.account_fragment_container, fragment)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }


}