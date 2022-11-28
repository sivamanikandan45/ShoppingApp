package com.example.shopping

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.enums.Orientation
import com.example.shopping.model.Category
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.RecentlyViewedViewModel
import com.example.shopping.viewmodel.SearchStateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class HomeFragment : Fragment() {
    private val cartViewModel:CartViewModel by activityViewModels()
    private val searchStateViewModel:SearchStateViewModel by viewModels()
    private lateinit var container:LinearLayout
    private lateinit var topOfferListAdapter:TopOfferListAdapter
    private lateinit var topOfferLayoutManager: LinearLayoutManager
    private lateinit var topOfferRecyclerView: RecyclerView

    private  val recentlyViewedViewModel:RecentlyViewedViewModel by activityViewModels()
    private lateinit var recentlyViewedRecyclerView: RecyclerView
    private lateinit var recentlyViewedAdapter:RecentlyViewedListAdapter
    private lateinit var recentlyViewedLayoutManager: LinearLayoutManager

    private lateinit var categoryList:List<Category>
    private lateinit var images:List<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu,menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView=menu.findItem(R.id.product_search)?.actionView as SearchView
        val searchItem=menu.findItem(R.id.product_search)
        searchItem.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                println("On search called")
                searchStateViewModel.searchBarExpanded=true
                val r: Resources = resources
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16F,
                    r.displayMetrics
                )
                val right=convertDpToPixel(16F,requireContext())
                val rightInt=right.toInt()
                searchView.setPadding(0,0, -rightInt,0)
                searchView.isIconified=false
                return true
            }

            fun convertDpToPixel(dp: Float, context: Context): Float {
                return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                searchStateViewModel.searchBarExpanded=false
                return true
            }

        })

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchStateViewModel.searchedQuery=newText
                    println("Query saved ${searchStateViewModel.searchedQuery}")
                }
                return true
            }
        })

        if(searchStateViewModel.searchBarExpanded){
            println("Search bar is opeend")
            println("string found is ${searchStateViewModel.searchedQuery}")
            searchView.isIconified=false
            searchItem.expandActionView()
            searchView.setQuery(searchStateViewModel.searchedQuery,false)
            println("The query is set to ${searchView.query}")
            //searchView.clearFocus()
            searchView.isFocusable=true
            //searchData(searchStateViewModel.searchedQuery)
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
    }

    override fun onStart() {
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onStart()
        var amount=0
        GlobalScope.launch {
            val job=launch{
                amount=cartViewModel.getCartItemCount()
                recentlyViewedViewModel.getRecentlyViewedFromDB()
            }
            job.join()
            println("Cart count  from home is $amount")
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount=container.childCount
        for(i in 0 until childCount){
            val imageView= container[i] as ImageView
            try{
                if(i==position){
                    imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.dot_selected))
                }else{
                    imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.dot_default))
                }
            }catch (ex:IllegalStateException){

            }
        }
    }

    private fun setUpIndicators(size:Int) {
        val indicators= arrayOfNulls<ImageView>(size)
        val layoutParams=
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for(i in indicators.indices){
            indicators[i]= ImageView(requireContext())
            indicators[i].apply {
                this?.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.dot_default))
                this?.layoutParams=layoutParams
            }
            container.addView(indicators[i])
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*val layout=view.findViewById<ConstraintLayout>(R.id.empty_page)
        val check=view.findViewById<NestedScrollView>(R.id.scroll_home)
        if(CheckInternet.isNetwork(requireContext())&& CheckInternet.isConnectedNetwork(requireContext())){
            println("Internet is available")
            layout.visibility=View.GONE
            check.visibility=View.VISIBLE
        }else{
            layout.visibility=View.VISIBLE
            check.visibility=View.GONE
        }*/
        (activity as AppCompatActivity)?.supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title="Shopping"

        val productViewModel:ProductViewModel by activityViewModels()

        val autoScrollableCarousel=view.findViewById<ViewPager2>(R.id.autoScrollingViewPager)
        container=view.findViewById(R.id.dots_container)
        images= listOf(R.drawable.poster1,R.drawable.poster2,R.drawable.poster3)
        val autoScrollableCarouselAdapter=AutoScrollableCarouselAdapter(images)
        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            autoScrollableCarouselAdapter.setOrientation(Orientation.LANDSCAPE)
        }
        setUpIndicators(images.size)
        setCurrentIndicator(0)
        autoScrollableCarouselAdapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                showCategoryListForCarouselImage(position)
            }
        })

        autoScrollableCarousel.adapter = autoScrollableCarouselAdapter
       autoScrollableCarousel.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                autoScrollableCarousel.post {
                    autoScrollableCarousel.currentItem = (autoScrollableCarousel.currentItem + 1) % images.size
                }
            }
        }
        val timer = Timer()
        timer.schedule(timerTask, 1000, 3000)

        categoryList= listOf(
            Category("Men's fashion",R.drawable.men),
            Category("Women's fashion",R.drawable.women),
            Category("Electronics",R.drawable.laptop),
            Category("Furniture & Home Decor",R.drawable.furniture),
            Category("Sunglasses",R.drawable.sunglass),
            Category("Groceries",R.drawable.grocery),
            Category("Beauty",R.drawable.skincare),
            Category("Others",R.drawable.other),
        )

        val categoryListAdapter=CategoryListAdapter(categoryList)
        categoryListAdapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                val intent=Intent(context,CategoryActivity::class.java)
                intent.putExtra("fragment_name","product_list")
                intent.putExtra("category", categoryList[position].categoryName)
                startActivity(intent)
            }
        })

        val manager=GridLayoutManager(context,4)
        val categoryRecyclerView=view.findViewById<RecyclerView>(R.id.category_recycler_view)
        categoryRecyclerView.setHasFixedSize(true)
        categoryRecyclerView.adapter=categoryListAdapter
        categoryRecyclerView.layoutManager=manager

        productViewModel.productList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            println(productViewModel.productList.value)
            topOfferListAdapter.setData(ArrayList(productViewModel.getTopOfferFromDB()))
        })

        productViewModel.topOfferList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            topOfferListAdapter.setData(ArrayList(productViewModel.topOfferList.value!!))
            topOfferListAdapter.notifyDataSetChanged()
            //topOfferListAdapter.setOnItemClickListener()
        })

        topOfferRecyclerView=view.findViewById(R.id.top_offer_recycler)
        topOfferListAdapter=TopOfferListAdapter()
        /*GlobalScope.launch {
            val job=launch{
                println(productViewModel.getTopOfferFromDB())
            }
            job.join()
        }*/
        topOfferListAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                val intent=Intent(context,CategoryActivity::class.java)
                intent.putExtra("fragment_name","product")
                intent.putExtra("selected_product_id",productViewModel.topOfferList.value?.get(position)?.productId)
                startActivity(intent)
                /*parentFragmentManager.commit {
                    *//*hide(this@HomeFragment)
                    productViewModel.selectedProduct.value= productViewModel.topOfferList.value?.get(position)
                    add<ProductFragment>(R.id.fragment_container)
                    addToBackStack(null)*//*
                    *//*val btm=requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                    btm.visibility=View.GONE*//*
                   *//* addToBackStack("fragment")
                    productViewModel.selectedProduct.value= productViewModel.topOfferList.value?.get(position)
                    replace(R.id.fragment_container,ProductFragment() )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)*//*
                }*/
            }
        })
        /*topOfferListAdapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                println("Clicked aat $position")
            }
        })*/
        topOfferRecyclerView.adapter=topOfferListAdapter

        topOfferRecyclerView.setHasFixedSize(true)
        topOfferLayoutManager=LinearLayoutManager(requireContext())
        topOfferLayoutManager.orientation=LinearLayoutManager.HORIZONTAL
        topOfferRecyclerView.layoutManager=topOfferLayoutManager

        recentlyViewedRecyclerView=view.findViewById(R.id.recently_viewed_recycler)
        recentlyViewedAdapter=RecentlyViewedListAdapter()
        recentlyViewedAdapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedProduct=recentlyViewedViewModel.recentlyViewedProductList.value?.get(position)
                val intent=Intent(context,CategoryActivity::class.java)
                intent.putExtra("fragment_name","product")
                intent.putExtra("selected_product_id",selectedProduct?.productId)
                startActivity(intent)
                /*parentFragmentManager.commit {
                    addToBackStack(null)
                    val selectedProduct=recentlyViewedViewModel.recentlyViewedProductList.value?.get(position)
                    if(selectedProduct!=null){
                        *//*val product=Product(selectedProduct.productId,selectedProduct.title,selectedProduct.description,selectedProduct.originalPrice,selectedProduct.discountPercentage,selectedProduct.priceAfterDiscount,selectedProduct.rating,selectedProduct.stock,selectedProduct.brand,selectedProduct.category,selectedProduct.thumbnail,selectedProduct.isFavorite)
                        productViewModel.selectedProduct.value=product
                        replace(R.id.fragment_container,ProductFragment())
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)*//*

                        *//*hide(this@HomeFragment)
                        productViewModel.selectedProduct.value=product
                        add<ProductFragment>(R.id.fragment_container)*//*

                    }
                }*/
            }
        })

        GlobalScope.launch {
            val job=launch(Dispatchers.IO) {
                recentlyViewedAdapter.setData(recentlyViewedViewModel.recentlyViewedItems())
            }
            job.join()
        }

        recentlyViewedViewModel.recentlyViewedProductList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val group=view.findViewById<Group>(R.id.recently_viewed_group)
            if(it.isEmpty()){
//                val group=view.findViewById<Group>(R.id.recently_viewed_group)
                group.visibility=View.GONE
            }else{
                group.visibility=View.VISIBLE
                recentlyViewedAdapter.setData(it)
                println("recebt list updated")
                recentlyViewedAdapter.notifyDataSetChanged()
            }
        })

        recentlyViewedLayoutManager= LinearLayoutManager(requireContext())
        recentlyViewedLayoutManager.orientation=LinearLayoutManager.HORIZONTAL
        recentlyViewedRecyclerView.adapter=recentlyViewedAdapter
        recentlyViewedRecyclerView.setHasFixedSize(true)
        recentlyViewedRecyclerView.layoutManager=recentlyViewedLayoutManager

        val clearRecentlyViewedBtn=view.findViewById<Button>(R.id.clear_all)
        clearRecentlyViewedBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear History")
                .setMessage("Are you sure you want to remove all?")
                .setPositiveButton("CLear"){_,_->
                    deleteAllRecentlyViewed()
                }.setNegativeButton("CANCEL"){_,_->

                }
                .show()
        }

        var amount=0
        GlobalScope.launch {
            val job=launch{
                amount=cartViewModel.getCartItemCount()
            }
            job.join()
            println("Cart count  from home is $amount")
        }
    }

    private fun showCategoryListForCarouselImage(position: Int) {
        val intent = Intent(context, CategoryActivity::class.java)
        intent.putExtra("fragment_name","product_list")
        when (images[position]) {
            R.drawable.poster1 -> {
                intent.putExtra("category", "Men's fashion")
            }
            R.drawable.poster2 -> {
                intent.putExtra("category", "Beauty")
            }
            R.drawable.poster3 -> {
                intent.putExtra("category", "Sunglasses")
            }
        }
        startActivity(intent)
    }

    private fun deleteAllRecentlyViewed() {
        GlobalScope.launch {
            recentlyViewedViewModel.clearAllRecentlyViewed()
        }
    }

}