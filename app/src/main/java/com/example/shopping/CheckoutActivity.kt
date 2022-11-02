package com.example.shopping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.shopping.enums.CheckoutMode
import com.example.shopping.viewmodel.CheckoutViewModel
import com.example.shopping.viewmodel.CustomViewModel

class CheckoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        val viewModel= ViewModelProvider(this)[CheckoutViewModel::class.java]
        if(intent.getStringExtra("checkoutMode")=="buy_now"){
            viewModel.mode=CheckoutMode.BUY_NOW
            viewModel.buyNowProductId=intent.getIntExtra("productId",1)
            viewModel.buyNowProductQuantity=intent.getIntExtra("quantity",0)
        }
        replaceFragment(SelectAddressFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            //add<ProductListFragment>(R.id.category_fragment_container)
            replace(R.id.checkout_fragment_container,fragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(supportFragmentManager.backStackEntryCount>0){
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