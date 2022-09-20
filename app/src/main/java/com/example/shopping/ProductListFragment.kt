package com.example.shopping

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Product

import com.example.shopping.viewmodel.ProductViewModel


class ProductListFragment : Fragment() {
    val productViewModel:ProductViewModel by activityViewModels()
    private lateinit var adapter:ProductListAdapter
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var manager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //(activity as AppCompatActivity).supportActionBar?.title="Hello"
        productRecyclerView=view.findViewById<RecyclerView>(R.id.product_list_recyclerView)
        adapter=ProductListAdapter()
        adapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                parentFragmentManager.commit {
                    addToBackStack(null)
                    val viewModel:ProductViewModel by activityViewModels()
                    viewModel.selectedProduct.value= viewModel.categoryList.value?.get(position)
                    replace(R.id.category_fragment_container,ProductFragment() )
                }
            }
        })

        adapter.setData(ArrayList(productViewModel.getCategoryWiseProductList()))
        manager=GridLayoutManager(context,2)
        productRecyclerView.setHasFixedSize(true)
        productRecyclerView.adapter=adapter
        productRecyclerView.layoutManager=manager

        productViewModel.categoryList.observe(viewLifecycleOwner, Observer {
            //println("change observed form the observer and the list is $it")
            adapter.setData(ArrayList(it))
            adapter.notifyDataSetChanged()
        })


        /*productViewModel.productList.observe(viewLifecycleOwner, Observer {
            adapter.setData(ArrayList(it))
            adapter.notifyDataSetChanged()
        })*/

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.category_activity_menu,menu)
        val searchView=menu.findItem(R.id.category_search)?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchData(newText)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.category_search->{
                //println("Search selected")
            }
//            R.id.filter->{
//                println("filter is selected")
//            }
            R.id.sort->{
                val bottomSheetFragment=SortBottomSheetFragment()
                bottomSheetFragment.show(parentFragmentManager,"")
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun searchData(newText: String?) {
        var list= mutableListOf<Product>()
        for(product in productViewModel.categoryList.value!!){
            if(product.title.lowercase().contains(newText!!.lowercase())){
                list.add(product)
            }
        }
        adapter.setData(ArrayList(list))
        adapter.notifyDataSetChanged()
    }


}