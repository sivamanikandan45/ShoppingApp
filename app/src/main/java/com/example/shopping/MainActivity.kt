package com.example.shopping

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.CustomViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //replaceFragment(HomeFragment())

        val viewModel= ViewModelProvider(this)[CustomViewModel::class.java]
        val cartViewModel= ViewModelProvider(this)[CartViewModel::class.java]
        var amount=0

        GlobalScope.launch {
            val job=launch{
               amount=cartViewModel.getCartItemCount()
            }
            job.join()
            println("Cart count is $amount")
        }


        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->{
                    replaceFragment(HomeFragment())
                    viewModel.fragmentId=R.id.home
                }
                R.id.cart->{
                    replaceFragment(CartFragment())
                    viewModel.fragmentId=R.id.cart
                }
                R.id.wishlist->{
                    replaceFragment(WishlistFragment())
                    viewModel.fragmentId=R.id.wishlist
                }
                R.id.account->{
                    replaceFragment(AccountFragment())
                    viewModel.fragmentId=R.id.account
                }
            }
            true
        }

        println("cart items is ${cartViewModel.noOfItem}")

        /*cartViewModel.cartItems.observe(this, Observer {
            println("Value changed")
            if(cartViewModel.noOfItem.value!=0){
                val badgeDrawable=bottomNavigationView.getOrCreateBadge(R.id.cart)
                badgeDrawable.isVisible=true
                badgeDrawable.number=cartViewModel.noOfItem.value!!
                badgeDrawable.backgroundColor=Color.parseColor("#b00020")
                badgeDrawable.badgeTextColor=Color.WHITE
            }
        })*/

        cartViewModel.noOfItem.observe(this, Observer {
            if(it==0){
                val badgeDrawable=bottomNavigationView.getOrCreateBadge(R.id.cart)
                badgeDrawable.isVisible=false
            }else{
                val badgeDrawable=bottomNavigationView.getOrCreateBadge(R.id.cart)
                badgeDrawable.isVisible=true
                badgeDrawable.number=it
                badgeDrawable.backgroundColor=Color.parseColor("#b00020")
                badgeDrawable.badgeTextColor=Color.WHITE
            }
        })

        /*GlobalScope.launch {
            var amount=0
            val job=launch(Dispatchers.IO) {
                amount=cartViewModel.getCartItemCount()
            }
            job.join()
            val job1=launch(Dispatchers.Main) {
                if(amount!=0){
                    val badgeDrawable=bottomNavigationView.getOrCreateBadge(R.id.cart)
                    badgeDrawable.isVisible=true
                    badgeDrawable.number= amount
                    badgeDrawable.backgroundColor=Color.parseColor("#b00020")
                    badgeDrawable.badgeTextColor=Color.WHITE
                }
            }
            job1.join()
        }*/







        if(savedInstanceState!=null){
            when(viewModel.fragmentId){
                R.id.home-> {
                    bottomNavigationView.selectedItemId=R.id.home
                }
                R.id.cart->{
                    bottomNavigationView.selectedItemId=R.id.cart
                }
                R.id.wishlist->{
                    bottomNavigationView.selectedItemId=R.id.wishlist
                }
                R.id.account->{
                    bottomNavigationView.selectedItemId=R.id.account
                }
            }
        }else{
            replaceFragment(HomeFragment())
        }

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container,fragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Main activity is destotying.....!!!1")
    }
}