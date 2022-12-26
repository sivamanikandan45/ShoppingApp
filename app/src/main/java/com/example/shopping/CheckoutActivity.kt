package com.example.shopping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.shopping.enums.CheckoutMode
import com.example.shopping.viewmodel.CheckoutViewModel
import com.example.shopping.viewmodel.CustomViewModel
import com.example.shopping.viewmodel.OrderViewModel
import com.example.shopping.viewmodel.ProductViewModel

class CheckoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        val productViewModel=ViewModelProvider(this)[ProductViewModel::class.java]
        val orderViewModel=ViewModelProvider(this)[OrderViewModel::class.java]
        val viewModel= ViewModelProvider(this)[CheckoutViewModel::class.java]
        if(intent.getStringExtra("checkoutMode")=="buy_now"){
            viewModel.mode=CheckoutMode.BUY_NOW
            viewModel.buyNowProductId=intent.getIntExtra("productId",1)
            viewModel.buyNowProductQuantity=intent.getIntExtra("quantity",0)
        }
        if(savedInstanceState==null){
            replaceFragment(SelectAddressFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            //add<ProductListFragment>(R.id.category_fragment_container)
            replace(R.id.checkout_fragment_container,fragment)
            //addToBackStack(null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(supportFragmentManager.backStackEntryCount>0){
                    val fragment=supportFragmentManager.findFragmentById(R.id.checkout_fragment_container)
                    if(fragment is OrderDetailFragment){
                        fragment.startMainActivity()
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