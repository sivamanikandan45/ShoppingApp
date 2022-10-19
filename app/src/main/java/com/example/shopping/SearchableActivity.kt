package com.example.shopping

import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import com.example.shopping.viewmodel.CustomViewModel
import com.example.shopping.viewmodel.FavoriteViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchableActivity : AppCompatActivity() {
    private lateinit var recycle: RecyclerView
    private lateinit var adapter: TopOfferListAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var emptypage:ConstraintLayout
    private lateinit var favoriteButtonListener:FavoriteButtonListener
   // private lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        /*val viewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        val favoriteViewModel= ViewModelProvider(this)[FavoriteViewModel::class.java]*/
        emptypage=findViewById(R.id.empty_search)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycle=findViewById(R.id.search_result_recyclerView)
        /*var x= DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recycle.addItemDecoration(x);*/
        recycle.setHasFixedSize(true)
        manager = if (this.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager(this,2)
        } else {
            //mRecycler.setLayoutManager(GridLayoutManager(mContext, 4))
            GridLayoutManager(this,4)
        }
        //manager=GridLayoutManager(this,2)
        adapter= TopOfferListAdapter()
        adapter.setViewType(1)
        recycle.layoutManager=manager
        recycle.adapter=adapter
        adapter.setOnItemClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                //println("$position is clicked")
            }
        })

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                supportActionBar?.title="Search Results for \"$query\""
                doMySearch(query)
            }
        }
    }

    private fun doMySearch(query: String) {
        println("Performing search for $query")
        val viewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        viewModel.productList.observe(this, Observer {
            val newList= mutableListOf<Product>()
            if(it!=null){
                for(i in it){
                    if(i.title.lowercase().contains(query.lowercase())){
                        newList.add(i)
                    }
                }
            }
            println("list is $newList")
            if(newList.isEmpty()){
                recycle.visibility= View.GONE
                emptypage.visibility=View.VISIBLE
            }
            adapter.setData(ArrayList(newList))
            adapter.notifyDataSetChanged()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}