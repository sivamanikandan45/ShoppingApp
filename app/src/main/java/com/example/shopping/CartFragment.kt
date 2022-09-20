package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.viewmodel.CartViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {
private lateinit var recyclerView: RecyclerView
private lateinit var manager:LinearLayoutManager
private lateinit var adapter: CartAdapter
private val cartViewModel:CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Cart"
        println("got cart as ${cartViewModel.cartItems.value}")

        /*if(cartViewModel.cartItems.value==null || cartViewModel?.cartItems.value!!.isEmpty()){
            val tv1=view.findViewById<TextView>(R.id.empty_label)
            tv1.visibility=View.VISIBLE
            *//*recyclerView=view.findViewById(R.id.cart_recycler_view)
            recyclerView.visibility=View.GONE*//*
        }else{*/
            val tv1=view.findViewById<TextView>(R.id.empty_label)
            tv1.visibility=View.GONE
            /*val tv2=view.findViewById<TextView>(R.id.empty_label_11)
            tv2.visibility=View.VISIBLE*/

            recyclerView=view.findViewById(R.id.cart_recycler_view)
            adapter= CartAdapter()
            manager= LinearLayoutManager(context)

            GlobalScope.launch {
                val job=launch {
                    adapter.setData(cartViewModel.getCartItems())
                }
                job.join()
                withContext(Dispatchers.Main){
                    val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL /*or LinearLayoutManager.HORIZONTAL*/) }
                    divider?.dividerInsetStart=375
                    recyclerView.addItemDecoration(divider!!)
                    recyclerView.adapter=adapter
                    recyclerView.layoutManager=manager
                }

            //}
            /*recyclerView.setHasFixedSize(true)
            recyclerView.adapter=adapter
            recyclerView.layoutManager=manager*/

        }

        cartViewModel.cartItems.observe(viewLifecycleOwner, Observer {
            adapter.setData(cartViewModel.cartItems.value!!)
            adapter.notifyDataSetChanged()
        })
    }

}