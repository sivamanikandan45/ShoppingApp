package com.example.shopping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView

class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.home_menu,menu)
        val searchView=menu?.findItem(R.id.product_search)?.actionView as SearchView
        //searchView.setIconifiedByDefault(true)
        searchView.isIconified=false
        return super.onCreateOptionsMenu(menu)
    }
}