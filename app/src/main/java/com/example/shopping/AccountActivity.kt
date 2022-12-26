package com.example.shopping

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.shopping.R
import com.example.shopping.viewmodel.FavoriteViewModel
import com.example.shopping.viewmodel.OrderViewModel
import com.example.shopping.viewmodel.ProductViewModel

class AccountActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val sharePreferences=getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharePreferences?.getInt("userId",-1)
        val favoriteViewModel=ViewModelProvider(this)[FavoriteViewModel::class.java]
        if (currentUserId != null) {
            favoriteViewModel.setUserId(currentUserId)
        }
        super.onCreate(savedInstanceState)
        val productViewModel=ViewModelProvider(this)[ProductViewModel::class.java]
        setContentView(R.layout.activity_account)
        when(intent.getStringExtra("frag_name")){
            "wishlist"->{
                if(savedInstanceState==null){
                    favoriteViewModel.calledFrom="Account"
                    replaceFragment(WishlistFragment())
                }
                }
            "savedAddress"->{
                if(savedInstanceState==null){
                    replaceFragment(SavedAddressFragment())
                }
                }
            "myOrders"->{
                if(savedInstanceState==null){
                    //orderViewModel.selectedId=intent?.getIntExtra("order_id",-1)!!
                    replaceFragment(MyOrdersFragment())
                }
            }
        }
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            //addToBackStack(null)
            replace(R.id.account_fragment_container, fragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(supportFragmentManager.backStackEntryCount>0){
                    /*val productViewModel= ViewModelProvider(this)[ProductViewModel::class.java]
                    productViewModel.pop()*/
                    val fragment=supportFragmentManager.findFragmentById(R.id.account_fragment_container)
                    if(fragment is AddDeliveryAddressFragment){
                        fragment.checkOnBackPressed()
                    }else{
                        supportFragmentManager.popBackStack()
                    }
                }else{
                    onBackPressed()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}