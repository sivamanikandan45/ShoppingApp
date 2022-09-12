package com.example.shopping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //replaceFragment(HomeFragment())

        val viewModel= ViewModelProvider(this)[CustomViewModel::class.java]

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
}