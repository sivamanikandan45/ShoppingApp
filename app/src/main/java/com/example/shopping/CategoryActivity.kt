package com.example.shopping

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.shopping.enums.CategoryType
import com.example.shopping.viewmodel.ProductViewModel

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val productViewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        super.onCreate(savedInstanceState)
        val category=intent.getStringExtra("category")
        setCategory(category,productViewModel)
        supportActionBar?.title=category
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_category)
        replaceFragment(ProductListFragment())
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