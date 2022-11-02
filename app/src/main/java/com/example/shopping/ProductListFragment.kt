package com.example.shopping

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.enums.Sort
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import com.example.shopping.viewmodel.FavoriteViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.SortViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.addAll


class ProductListFragment : Fragment() {
    private val productViewModel:ProductViewModel by activityViewModels()
    private val favoriteViewModel:FavoriteViewModel by activityViewModels()
    private val sortViewModel:SortViewModel by activityViewModels()
    private lateinit var adapter:ProductListAdapter
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var manager: GridLayoutManager
    private lateinit var sortItem:MenuItem
    private lateinit var searchItem: MenuItem
    private var searchedQuery=""
    private lateinit var currentProductList:List<Product>

    override fun onPrepareOptionsMenu(menu: Menu) {
        searchItem=menu.findItem(R.id.category_search)
        sortItem=menu.findItem(R.id.sort)
        searchItem.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                println("On search called from onPrepareOptions menu")
                sortItem.isVisible=false
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                sortItem.isVisible=true
                setAdapterAttributes()
                return true
            }

        })
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

        setAdapterAttributes()

        adapter.setData(productViewModel.getCategoryWiseProductList())
        manager = if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager(requireContext(),2)
        } else {
            GridLayoutManager(requireContext(),4)
        }
       // manager=GridLayoutManager(context,2)
        productRecyclerView.setHasFixedSize(true)
        productRecyclerView.adapter=adapter
        productRecyclerView.layoutManager=manager

        sortBySelectedOption()
        /*productRecyclerView.setItemViewCacheSize(10);
        productRecyclerView.isDrawingCacheEnabled = true;*/

        productViewModel.categoryList.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
            //sortBySelectedOption()
        })

        /*productViewModel.productList.observe(viewLifecycleOwner, Observer {
            adapter.setData(ArrayList(it))
            adapter.notifyDataSetChanged()
        })*/

    }

    private fun setAdapterAttributes() {
        productViewModel.categoryList.value?.let {
            adapter.setData(it)
        }
        adapter.setFavoriteButtonListener(object : FavoriteButtonListener {
            override fun handle(position: Int) {
                val viewModel: ProductViewModel by activityViewModels()
                val product = viewModel.categoryList.value?.get(position)
                if (product?.favorite == true) {
                    GlobalScope.launch {
                        val job = launch(Dispatchers.IO) {
                            favoriteViewModel.deleteFromFavorites(product.productId)
                            viewModel.removeFavorite(product.productId)
                        }
                        job.join()
                        Snackbar.make(
                            productRecyclerView,
                            "Removed from WishList",
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                } else {
                    GlobalScope.launch {
                        val job = launch(Dispatchers.IO) {
                            if (product != null) {
                                val favoriteProduct = FavoriteProduct(
                                    product.productId,
                                    product.title,
                                    product.description,
                                    product.originalPrice,
                                    product.discountPercentage,
                                    product.priceAfterDiscount,
                                    product.rating,
                                    product.stock,
                                    product.brand,
                                    product.category,
                                    product.thumbnail
                                )
                                favoriteViewModel.addToFavorites(favoriteProduct)
                                viewModel.markAsFavorite(product.productId)
                            }
                        }
                        job.join()
                        Snackbar.make(
                            productRecyclerView,
                            "Added to WishList",
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        })

        adapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                currentProductList= mutableListOf()
                parentFragmentManager.commit {
                    /*for(i in productViewModel.categoryList.value!!){
                        currentProductList.add(i)
                    }*/
                   currentProductList= productViewModel.categoryList.value!!.map { it.copy() }
                       // productViewModel.categoryList.value!!.toMutableList()
                    println("Wihle opening the current list is $currentProductList")
                    hide(this@ProductListFragment)
                    productViewModel.selectedProduct.value =
                        productViewModel.categoryList.value?.get(position)
                    add<ProductFragment>(R.id.category_fragment_container)
                    addToBackStack(null)
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                    /*productViewModel.selectedProduct.value= productViewModel.categoryList.value?.get(position)
                    replace(R.id.category_fragment_container,ProductFragment() )
                    addToBackStack(null)*/
                }
            }
        })
    }

    private fun sortBySelectedOption() {
        when (productViewModel.selectedSort) {
            Sort.NONE -> {

            }
            Sort.ALPHA -> {
                productViewModel.sortByAlphabet()
            }
            Sort.RATING -> {
                productViewModel.sortByARating()
            }
            Sort.PRICE_HIGH_TO_LOW -> {
                productViewModel.sortByPriceHghToLow()
            }
            Sort.PRICE_LOW_TO_HIGH -> {
                productViewModel.sortByPriceLowToHigh()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.category_activity_menu,menu)
        val searchView=menu.findItem(R.id.category_search)?.actionView as SearchView
        sortItem=menu.findItem(R.id.sort)
        searchItem=menu.findItem(R.id.category_search)
        searchItem.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                println("On search called")
                searchView.isIconified=false
                sortItem.isVisible=false
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                sortItem.isVisible=true
                setAdapterAttributes()
                return true
            }

        })

        if(searchedQuery.isNotEmpty()){
            val searchView=searchItem.actionView as SearchView
            searchView.isIconified=false
            searchItem.expandActionView()
            searchView.setQuery(searchedQuery,false)
            searchView.isFocusable=true
            //searchData(searchedQuery)
        }

        /*if(searchedQuery!=""){
            searchView.isIconified=false
            searchView.onActionViewExpanded()
            searchView.requestFocus()
            searchView.setQuery(searchedQuery,false)
        }*/

        /*searchView.setOnSearchClickListener {
            println("On search called")
            sortItem.isVisible=false
            //activity?.invalidateOptionsMenu()
        }*/



        /*searchItem.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                sortItem.isVisible=true
                return true
            }

        })*/



        //searchView.

        /*searchView.setOnCloseListener {
            searchView.onActionViewCollapsed()
            sortItem.isVisible=true
            false
        }*/







        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    productViewModel.searchedQuery=newText
                    if(!searchView.isIconified){
                        println("The searched value is ${productViewModel.searchedQuery}")
                        searchData(newText)
                   }
                }
                return true
            }
        })

       // searchView.setIconifiedByDefault(true)
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
        if(newText?.isNotEmpty() == true){
            for(product in productViewModel.categoryList.value!!){
                if(product.title.lowercase().contains(newText!!.lowercase())){
                    list.add(product)
                }
            }
            adapter.setData(list)
            currentProductList=list
            adapter.setFavoriteButtonListener(object :FavoriteButtonListener{
                override fun handle(position: Int) {
                    val viewModel:ProductViewModel by activityViewModels()
                    val product=list[position]
                    if(product.favorite){
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
                        currentProductList=list
                        println("Clicked click listener's item click method")
                        hide(this@ProductListFragment)
                        productViewModel.selectedProduct.value= list[position]
                        add<ProductFragment>(R.id.category_fragment_container)
                        addToBackStack(null)
                    }
                }
            })
            productRecyclerView.adapter=adapter
        }
        //productRecyclerView.adapter=adapter
        //adapter.notifyDataSetChanged()
    }



    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){

            if(searchedQuery.isNotEmpty()){
                //activity?.invalidateOptionsMenu()
                println("Setting value got $searchedQuery")
            }else{
                println("Current data is $currentProductList")
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        productViewModel.getCategoryWiseProductList()
                    }
                    job.join()
                    withContext(Dispatchers.Main){
                        adapter.setList(currentProductList,productViewModel.categoryList.value!!)
                    }
                }
            }
            //val category=activity?.intent?.getStringExtra("category")


            //adapter.setData(productViewModel.getCategoryWiseProductList())
            /*if(searchedQuery.isNotEmpty()){
                val searchView=searchItem.actionView as SearchView
                searchView.isIconified=false
                searchItem.expandActionView()
                searchView.setQuery(searchedQuery,false)
                searchView.isFocusable=true
            }*/
        }
        else{
            searchedQuery=productViewModel.searchedQuery
        }
    }




}