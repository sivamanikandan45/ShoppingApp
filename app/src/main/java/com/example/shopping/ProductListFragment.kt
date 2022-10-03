package com.example.shopping

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import com.example.shopping.viewmodel.FavoriteViewModel

import com.example.shopping.viewmodel.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProductListFragment : Fragment() {
    private val productViewModel:ProductViewModel by activityViewModels()
    private val favoriteViewModel:FavoriteViewModel by activityViewModels()
    private lateinit var adapter:ProductListAdapter
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var manager: GridLayoutManager
    private lateinit var sortItem:MenuItem
    private lateinit var searchItem: MenuItem

    override fun onPrepareOptionsMenu(menu: Menu) {
        searchItem=menu.findItem(R.id.category_search)
        sortItem=menu.findItem(R.id.sort)
        super.onPrepareOptionsMenu(menu)
    }

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
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        productRecyclerView=view.findViewById<RecyclerView>(R.id.product_list_recyclerView)
        adapter=ProductListAdapter()

        adapter.setFavoriteButtonListener(object :FavoriteButtonListener{
            override fun handle(position: Int) {
                val viewModel:ProductViewModel by activityViewModels()
                val product=viewModel.categoryList.value?.get(position)
                if(product?.favorite == true){
                    GlobalScope.launch {
                        val job=launch(Dispatchers.IO) {
                            favoriteViewModel.deleteFromFavorites(product.productId)
                            viewModel.removeFavorite(product.productId)
                        }
                        job.join()
                        Snackbar.make(productRecyclerView,"Removed from WishList",Snackbar.LENGTH_LONG)
                            .show()
                    }
                }else{
                    GlobalScope.launch {
                        val job=launch(Dispatchers.IO) {
                            if(product!=null){
                                val favoriteProduct=FavoriteProduct(product.productId,product.title,product.description,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,product.rating,product.stock,product.brand,product.category,product.thumbnail)
                                favoriteViewModel.addToFavorites(favoriteProduct)
                                viewModel.markAsFavorite(product.productId)
                            }
                        }
                        job.join()
                        Snackbar.make(productRecyclerView,"Added to WishList",Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })

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
        adapter.setData(productViewModel.getCategoryWiseProductList())
        //adapter.setData(ArrayList(productViewModel.getCategoryWiseProductList()))
        manager=GridLayoutManager(context,2)
        productRecyclerView.setHasFixedSize(true)
        productRecyclerView.adapter=adapter
        productRecyclerView.layoutManager=manager

        productViewModel.categoryList.observe(viewLifecycleOwner, Observer {
            //println("change observed form the observer and the list is $it")
            //adapter.setData(ArrayList(it))
            adapter.setData(it)
            //adapter.notifyDataSetChanged()
        })


        /*productViewModel.productList.observe(viewLifecycleOwner, Observer {
            adapter.setData(ArrayList(it))
            adapter.notifyDataSetChanged()
        })*/

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.category_activity_menu,menu)
        val searchView=menu.findItem(R.id.category_search)?.actionView as SearchView

        searchView.setOnSearchClickListener {
            sortItem.isVisible=false
            //activity?.invalidateOptionsMenu()
        }
        searchView.setOnCloseListener{
            /*sortItem.isVisible=true
            searchItem.isVisible=true
            //searchView.isIconified=true
            activity?.invalidateOptionsMenu()*/
            //onCreateOptionsMenu(menu,inflater)
            true
        }


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
                /*if(!searchView.isIconified){
                    val view=menu.findItem(R.id.sort)
                    view.isVisible=false
                }*/
                //println("Search selected")
                //val searchView=m.findItem(R.id.category_search)?.actionView as SearchView
               // val sortIcon=
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
        val list= mutableListOf<Product>()
        for(product in productViewModel.categoryList.value!!){
            if(product.title.lowercase().contains(newText!!.lowercase())){
                list.add(product)
            }
        }
        adapter.setData(ArrayList(list))
        adapter.notifyDataSetChanged()
    }


}