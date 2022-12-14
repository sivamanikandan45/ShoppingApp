package com.example.shopping

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

class AccountActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val favoriteViewModel=ViewModelProvider(this)[FavoriteViewModel::class.java]
        super.onCreate(savedInstanceState)
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
                    supportFragmentManager.popBackStack()
                }else{
                    onBackPressed()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}