package com.example.shopping

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.model.Category
import com.example.shopping.model.Product
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.RecentlyViewedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private val cartViewModel:CartViewModel by activityViewModels()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title="Shopping"

        val productViewModel:ProductViewModel by activityViewModels()

        val autoScrollableCarousel=view.findViewById<ViewPager2>(R.id.autoScrollingViewPager)
        images= listOf(R.drawable.poster1,R.drawable.poster2,R.drawable.poster3)
        val autoScrollableCarouselAdapter=AutoScrollableCarouselAdapter(images)
        autoScrollableCarouselAdapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                showCategoryListForCarouselImage(position)
            }
        })

        autoScrollableCarousel.adapter = autoScrollableCarouselAdapter
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
                parentFragmentManager.commit {
                    /*hide(this@HomeFragment)
                    productViewModel.selectedProduct.value= productViewModel.topOfferList.value?.get(position)
                    add<ProductFragment>(R.id.fragment_container)
                    addToBackStack(null)*/
                    /*val btm=requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                    btm.visibility=View.GONE*/
                    addToBackStack("fragment")
                    productViewModel.selectedProduct.value= productViewModel.topOfferList.value?.get(position)
                    replace(R.id.fragment_container,ProductFragment() )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                    /*val intent=Intent(context,CategoryActivity::class.java)
                    intent.putExtra("fragment_name","product")
                    intent.putExtra("selected_product_id",productViewModel.topOfferList.value?.get(position)?.productId)
                    startActivity(intent)*/

                }
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
                parentFragmentManager.commit {
                    addToBackStack(null)
                    val selectedProduct=recentlyViewedViewModel.recentlyViewedProductList.value?.get(position)
                    if(selectedProduct!=null){
                        val product=Product(selectedProduct.productId,selectedProduct.title,selectedProduct.description,selectedProduct.originalPrice,selectedProduct.discountPercentage,selectedProduct.priceAfterDiscount,selectedProduct.rating,selectedProduct.stock,selectedProduct.brand,selectedProduct.category,selectedProduct.thumbnail,selectedProduct.isFavorite)
                        productViewModel.selectedProduct.value=product
                        replace(R.id.fragment_container,ProductFragment())
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        /*hide(this@HomeFragment)
                        productViewModel.selectedProduct.value=product
                        add<ProductFragment>(R.id.fragment_container)*/

                    }
                }
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