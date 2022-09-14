package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.viewmodel.ProductViewModel

class ProductListFragment : Fragment() {
    val productViewModel:ProductViewModel by activityViewModels()
    private lateinit var adapter:ProductListAdapter
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var manager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productRecyclerView=view.findViewById<RecyclerView>(R.id.product_list_recyclerView)
        adapter=ProductListAdapter()
        adapter.setData(ArrayList(productViewModel.getCategoryWiseProductList()))
        manager=GridLayoutManager(context,2)
        productRecyclerView.setHasFixedSize(true)
        productRecyclerView.adapter=adapter
        productRecyclerView.layoutManager=manager

        productViewModel.categoryList.observe(viewLifecycleOwner, Observer {
            println("changed")
            println(it)
            adapter.setData(ArrayList(it))
            adapter.notifyDataSetChanged()
        })

        productViewModel.productList.observe(viewLifecycleOwner, Observer {
            adapter.setData(ArrayList(it))
            adapter.notifyDataSetChanged()
        })

    }

}