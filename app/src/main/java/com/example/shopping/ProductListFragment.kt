package com.example.shopping

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.enums.Sort
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import com.example.shopping.viewmodel.FavoriteViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.SearchStateViewModel
import com.example.shopping.viewmodel.SortViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.Collections.addAll
import java.util.concurrent.TimeUnit


class ProductListFragment : Fragment() {
    private val productViewModel:ProductViewModel by activityViewModels()
    private val favoriteViewModel:FavoriteViewModel by activityViewModels()
    private val searchStateViewModel:SearchStateViewModel by viewModels()
    private lateinit var adapter:ProductListAdapter
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var manager: GridLayoutManager
    private lateinit var sortItem:MenuItem
    private lateinit var searchItem: MenuItem
    private var searchedQuery=""
    private lateinit var currentProductList:List<Product>

    override fun onPrepareOptionsMenu(menu: Menu) {
        /*searchItem=menu.findItem(R.id.category_search)
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

        })*/
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
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        productRecyclerView=view.findViewById<RecyclerView>(R.id.product_list_recyclerView)
        val sharedPreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharedPreferences?.getInt("userId",-1)
        if (currentUserId != null) {
            favoriteViewModel.setUserId(currentUserId)
        }

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

            override fun isFavorite(position: Int): Boolean {
                val viewModel: ProductViewModel by activityViewModels()
                val product = viewModel.categoryList.value?.get(position)
                val productId=product?.productId
                return favoriteViewModel.isFavorite(productId)
            }

            override fun handle(position: Int):Boolean {
                val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                val loginSkipped=sharePreferences?.getBoolean("login_skipped",false)
                val loginStatus=sharePreferences?.getBoolean("login_status",false)
                println("login status is $loginStatus")
                println("login  skioped status is $loginSkipped")
                if(loginSkipped!! || !loginStatus!!){
                    lifecycleScope.launch(Dispatchers.Main){
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Login Required")
                            .setMessage("Log in for the best experience")
                            .setPositiveButton("Login") { _, _ ->
                                val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                                with(sharePreferences?.edit()){
                                    this?.putBoolean("login_skipped",false)
                                    this?.apply()
                                }
                                val intent= Intent(requireContext(),MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                            .show()
                    }
                    return false
                    /*Snackbar.make(productRecyclerView,"Login 1!!",Snackbar.LENGTH_LONG)
                        .show()*/
                }else{
                    val viewModel: ProductViewModel by activityViewModels()
                    val product = viewModel.categoryList.value?.get(position)
                    val productId=product?.productId
                    if (favoriteViewModel.isFavorite(productId)) {
                        GlobalScope.launch {
                            val job = launch(Dispatchers.IO) {
                                favoriteViewModel.deleteFromFavorites(product?.productId)
                                //viewModel.removeFavorite(product.productId)
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
                                    val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                                    val currentUserId=sharePreferences?.getInt("userId",-1)
                                    val favoriteProduct = FavoriteProduct(0,
                                        product.productId,
                                        currentUserId!!,
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
                                    //viewModel.markAsFavorite(product.productId)
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
                    return true
                }                /*if(product?.favorite==true){
                    favoriteButton.setImageResource(R.drawable.heart_red)
                }else{
                    favoriteButton.setImageResource(R.drawable.border_heart)
                }*/


            }
        })

        adapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                currentProductList= mutableListOf()
                parentFragmentManager.commit{
                    /*for(i in productViewModel.categoryList.value!!){
                        currentProductList.add(i)
                    }*/
                   currentProductList= productViewModel.categoryList.value!!.map { it.copy() }
                       // productViewModel.categoryList.value!!.toMutableList()
                    println("Wihle opening the current list is $currentProductList")
                    setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                    hide(this@ProductListFragment)
                    productViewModel.selectedProduct.value =
                        productViewModel.categoryList.value?.get(position)
                    add<ProductFragment>(R.id.category_fragment_container)
                    addToBackStack(null)
                    //setReorderingAllowed(true)
                    //postponeEnterTransition(5000,TimeUnit.MILLISECONDS)
                    //startPostponedEnterTransition()
                    //setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    //setCustomAnimations(R.anim.slide_in,R.anim.fade_out)
                    //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)



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
                searchStateViewModel.searchBarExpanded=true
                println("Search bar staus is changed to ${searchStateViewModel.searchBarExpanded}")
                searchView.isIconified=false
                sortItem.isVisible=false
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                searchStateViewModel.searchBarExpanded=false
                sortItem.isVisible=true
                setAdapterAttributes()
                return true
            }

        })

        println("Status of search bar is ${searchStateViewModel.searchBarExpanded}")

        if(searchStateViewModel.searchBarExpanded){
            println("Search bar is opeend")
            //val searchView=searchItem.actionView as SearchView
            searchView.isIconified=false
            searchItem.expandActionView()
            searchView.setQuery(searchStateViewModel.searchedQuery,false)
            searchView.isFocusable=true
            searchData(searchStateViewModel.searchedQuery)
        }

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
                    searchStateViewModel.searchedQuery=newText
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
                override fun isFavorite(position: Int): Boolean {
                    val viewModel: ProductViewModel by activityViewModels()
                    val product = adapter.list[position]
                    val productId=product.productId
                    return favoriteViewModel.isFavorite(productId)
                }

                override fun handle(position: Int):Boolean {
                    val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                    val loginSkipped=sharePreferences?.getBoolean("login_skipped",false)
                    val loginStatus=sharePreferences?.getBoolean("login_status",false)
                    println("login status is $loginStatus")
                    println("login  skioped status is $loginSkipped")
                    if(loginSkipped!! || !loginStatus!!){
                        lifecycleScope.launch(Dispatchers.Main){
                            AlertDialog.Builder(requireActivity())
                                .setTitle("Login Required")
                                .setMessage("Log in for the best experience")
                                .setPositiveButton("Login") { _, _ ->
                                    val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                                    with(sharePreferences?.edit()){
                                        this?.putBoolean("login_skipped",false)
                                        this?.apply()
                                    }
                                    val intent= Intent(requireContext(),MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                }
                                .show()
                        }
                        return false
                    }else{
                        val viewModel:ProductViewModel by activityViewModels()
                        val product=adapter.list[position]
                        val productId=product?.productId
                        if (favoriteViewModel.isFavorite(productId)) {
                            GlobalScope.launch {
                                val job=launch(Dispatchers.IO) {
                                    favoriteViewModel.deleteFromFavorites(product.productId)
                                    //viewModel.removeFavorite(product.productId)
                                }
                                job.join()
                                Snackbar.make(productRecyclerView,"Removed from WishList",Snackbar.LENGTH_LONG)
                                    .show()
                            }
                        }else{
                            GlobalScope.launch {
                                val job=launch(Dispatchers.IO) {
                                    if(product!=null){
                                        val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                                        val currentUserId=sharePreferences?.getInt("userId",-1)
                                        val favoriteProduct=FavoriteProduct(0,product.productId,currentUserId!!,product.title,product.description,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,product.rating,product.stock,product.brand,product.category,product.thumbnail)
                                        favoriteViewModel.addToFavorites(favoriteProduct)
                                        //viewModel.markAsFavorite(product.productId)
                                    }
                                }
                                job.join()
                                Snackbar.make(productRecyclerView,"Added to WishList",Snackbar.LENGTH_LONG)
                                    .show()
                            }
                        }
                        return true
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
                println("Setting value got $searchedQuery")
            }else{
                try{
                    adapter.notifyDataSetChanged()
                    println("Current data is $currentProductList")
                    /*GlobalScope.launch {
                        val job=launch(Dispatchers.IO) {
                            productViewModel.getCategoryWiseProductList()
                        }
                        job.join()
                        withContext(Dispatchers.Main){
                            adapter.setList(currentProductList,productViewModel.categoryList.value!!)
                        }
                    }*/
                }catch (exception:UninitializedPropertyAccessException){
                    //activity?.onBackPressed()
                }

            }
            (activity as AppCompatActivity).supportActionBar?.show()
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