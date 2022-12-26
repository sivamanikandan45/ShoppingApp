package com.example.shopping

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import com.example.shopping.viewmodel.CustomViewModel
import com.example.shopping.viewmodel.FavoriteViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.example.shopping.viewmodel.SearchStateViewModel
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
    private val searchStateViewModel: SearchStateViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var searchItem:MenuItem
   // private lateinit var viewModel: ProductViewModel

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        searchView=menu?.findItem(R.id.general_product_search)?.actionView as SearchView
        searchItem=menu?.findItem(R.id.general_product_search)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.searchable_activity_menu,menu)
        val searchView=menu?.findItem(R.id.general_product_search)?.actionView as SearchView
        val searchItem=menu?.findItem(R.id.general_product_search)
        searchView.isIconified=false
        searchItem.expandActionView()
        searchView.queryHint="Search Product..."
        if(searchStateViewModel.searchedQuery!=""){
            searchView.setQuery(searchStateViewModel.searchedQuery,false)
        }
        searchView.isFocusable=true
        searchItem.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                /*println("On search called")
                println("query fetched is ${searchStateViewModel.searchedQuery}")
                query=searchStateViewModel.searchedQuery
                searchStateViewModel.searchBarExpanded=true
                val r: Resources = resources
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16F,
                    r.displayMetrics
                )
                val right=convertDpToPixel(16F,requireContext())
                val rightInt=right.toInt()
                searchView.setPadding(0,0, -rightInt,0)
                searchView.isIconified=false*/
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                onBackPressed()
                return false
            }

        })
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    doMySearch(newText)
                    searchStateViewModel.searchedQuery=newText
                    println("typed text is $newText")
                    //searchStateViewModel.searchedQuery=newText
                    //println("Query saved ${searchStateViewModel.searchedQuery}")
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        /*val viewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        val favoriteViewModel= ViewModelProvider(this)[FavoriteViewModel::class.java]*/
        emptypage=findViewById(R.id.empty_search)
        val productViewModel=ViewModelProvider(this)[ProductViewModel::class.java]




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
        productViewModel.productList.observe(this, Observer{
            adapter.setData(ArrayList(it))
            adapter.setOnItemClickListener(object:ItemClickListener{
                override fun onItemClick(position: Int) {
                    val intent=Intent(this@SearchableActivity,CategoryActivity::class.java)
                    intent.putExtra("fragment_name","product")
                    intent.putExtra("selected_product_id",it[position].productId)
                    startActivity(intent)
                }
            })
        })
        adapter.setViewType(1)
        recycle.layoutManager=manager
        recycle.adapter=adapter

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                supportActionBar?.title="Search Results for \"$query\""
                doMySearch(query)
            }
        }
    }

    private fun doMySearch(query: String) {
        val list= mutableListOf<Product>()
        println("Performing search for $query")
        val viewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        //viewModel.productList.observe(this, Observer {
        //println("The list is ${viewModel.productList.value}")
            if(viewModel.productList.value!=null){
                for(i in viewModel.productList.value!!){
                    /*if(product.title.lowercase().contains(newText!!.lowercase())){
                        list.add(product)
                    }*/
                    if(i.title.lowercase().contains(query.lowercase())){
                        list.add(i)
                    }
                }
            }
            println("list is $list")
            if(list.isEmpty()){
                recycle.visibility= View.GONE
                emptypage.visibility=View.VISIBLE
            }else{
                recycle.visibility= View.VISIBLE
                emptypage.visibility=View.GONE
            }
            adapter.setData(ArrayList(list))
            adapter.setOnItemClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                val intent=Intent(this@SearchableActivity,CategoryActivity::class.java)
                intent.putExtra("fragment_name","product")
                intent.putExtra("selected_product_id",list[position].productId)
                startActivity(intent)
            }
        })
        adapter.notifyDataSetChanged()
        recycle.adapter=adapter
        //})
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                return true
            }
            R.id.action_voice_search->{
                val speechIntent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                //speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Product Name to Search")
                resultLauncher.launch(speechIntent)
                println("Voice Search is clicked")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result?.resultCode == Activity.RESULT_OK) {
            val matchedString=result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            searchView.isIconified=false
            searchItem.expandActionView()
            searchView.queryHint="Search Product..."
            if(matchedString?.get(0)!=null){
                searchView.setQuery(matchedString[0],false)
                searchStateViewModel.searchedQuery=matchedString[0]
            }
            searchView.isFocusable=true
            //Toast.makeText(this, matchedString?.get(0) ?: "null",Toast.LENGTH_SHORT).show()
        }else{
            //messageTextView.text="You Clicked Back Button"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}