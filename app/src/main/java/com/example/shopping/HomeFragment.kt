package com.example.shopping

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.model.Category
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private val cartViewModel:CartViewModel by activityViewModels()
    private lateinit var topOfferListAdapter:TopOfferListAdapter
    private lateinit var topOfferLayoutManager: GridLayoutManager
    private lateinit var topOfferRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onStart() {
        super.onStart()
        var amount=0
        GlobalScope.launch {
            val job=launch{
                amount=cartViewModel.getCartItemCount()
            }
            job.join()
            println("Cart count  from home is $amount")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Shopping"

        val productViewModel:ProductViewModel by activityViewModels()


        val autoScrollableCarousel=view.findViewById<ViewPager2>(R.id.autoScrollingViewPager)
        val images= listOf(R.drawable.poster1,R.drawable.poster2,R.drawable.poster3)
        val autoScrollableCarouselAdapter=AutoScrollableCarouselAdapter(images)
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

        val categoryList= listOf(
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
        })

        productViewModel.topOfferList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            topOfferListAdapter.setData(ArrayList(productViewModel.topOfferList.value!!))
        })

        topOfferRecyclerView=view.findViewById(R.id.top_offer_recycler)
        topOfferListAdapter=TopOfferListAdapter()
        topOfferListAdapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                println("Clicked aat $position")
            }

        })
        topOfferRecyclerView.adapter=topOfferListAdapter
        topOfferRecyclerView.setHasFixedSize(true)
        //topOfferRecyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        val manager1=LinearLayoutManager(requireContext())
        manager1.orientation=LinearLayoutManager.HORIZONTAL
        topOfferRecyclerView.layoutManager=manager1

        var amount=0
        GlobalScope.launch {
            val job=launch{
                amount=cartViewModel.getCartItemCount()
            }
            job.join()
            println("Cart count  from home is $amount")
        }



    }
}