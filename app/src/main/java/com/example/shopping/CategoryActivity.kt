package com.example.shopping

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shopping.enums.CategoryType
import com.example.shopping.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {
    private lateinit var productViewModel:ProductViewModel
//    val productViewModel= ViewModelProvider(this)[ProductViewModel::class.java]
    override fun onCreate(savedInstanceState: Bundle?) {
        val productViewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val fragName=intent.getStringExtra("fragment_name")
        println("I have got fragName $fragName")
    if(fragName=="product_list"){
        val category=intent.getStringExtra("category")
        setCategory(category,productViewModel)
        supportActionBar?.title=category
        replaceFragment(ProductListFragment())
    }else if(fragName=="product"){
        val id=intent.getIntExtra("selected_product_id",0)
        GlobalScope.launch {
            val job=launch (Dispatchers.IO){
                productViewModel.getProductByID(id)
            }
            job.join()
            withContext(Dispatchers.Main){
                productViewModel.selectedProduct.observe(this@CategoryActivity, Observer {
                    println("The product $it")
                    replaceFragment(ProductFragment())
                })
            }
        }

    }

        /*if(intent!=null){
            val position=intent.getIntExtra("SelectedProduct",-1)
            if(position!=-1){
                println("?Replaced Product Frgment")
                productViewModel.selectedProduct.value=productViewModel.categoryList.value?.get(position)
                println(productViewModel.selectedProduct.value)
                replaceFragment(ProductFragment())
            }else{
                println("replcaed Product list Frag")
                replaceFragment(ProductListFragment())
            }
        }*/
    }

    private fun setCategory(category: String?,productViewModel:ProductViewModel) {
        when(category){
            "Men's fashion"->productViewModel.categoryType= CategoryType.MEN
            "Women's fashion"->productViewModel.categoryType= CategoryType.WOMEN
            "Electronics"->productViewModel.categoryType= CategoryType.ELECTRONICS
            "Furniture & Home Decor"->productViewModel.categoryType= CategoryType.FURNITURE
            "Sunglasses"->productViewModel.categoryType= CategoryType.SUNGLASSES
            "Groceries"->productViewModel.categoryType= CategoryType.GROCERIES
            "Beauty"->productViewModel.categoryType= CategoryType.BEAUTY
            "Others"->productViewModel.categoryType= CategoryType.OTHERS
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            //add<ProductListFragment>(R.id.category_fragment_container)
            replace(R.id.category_fragment_container,fragment)
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