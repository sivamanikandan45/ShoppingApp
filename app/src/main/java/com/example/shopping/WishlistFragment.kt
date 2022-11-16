package com.example.shopping

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.FavoriteProduct
import com.example.shopping.model.Product
import com.example.shopping.model.SelectedProduct
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.FavoriteViewModel
import com.example.shopping.viewmodel.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: WishlistAdapter
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private val productViewModel:ProductViewModel by activityViewModels()
    private val cartViewModel:CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    /*override fun onStart() {
        super.onStart()
        if(favoriteViewModel.calledFrom=="Main"){
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }else if(favoriteViewModel.calledFrom=="Account"){
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title="Wishlist"
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if(favoriteViewModel.calledFrom=="Main"){
            /*(activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false); // disable the button
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false); // remove the left caret
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false);*/
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }else if(favoriteViewModel.calledFrom=="Account"){
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val empty=view.findViewById<ConstraintLayout>(R.id.empty_page)
        val scroll=view.findViewById<RecyclerView>(R.id.wishlist_recyclerView)

        recyclerView=view.findViewById(R.id.wishlist_recyclerView)
        adapter= WishlistAdapter()
        adapter.setOnItemClickListener(object :ItemClickListener{
            override fun onItemClick(position: Int) {
                parentFragmentManager.commit {
                    addToBackStack(null)
                    val selectedProduct=favoriteViewModel.favoriteItems.value?.get(position)
                    if(selectedProduct!=null){
                        val product=Product(selectedProduct.productId,selectedProduct.title,selectedProduct.description,selectedProduct.originalPrice,selectedProduct.discountPercentage,selectedProduct.priceAfterDiscount,selectedProduct.rating,selectedProduct.stock,selectedProduct.brand,selectedProduct.category,selectedProduct.thumbnail,true)
                        productViewModel.selectedProduct.value=product
                        //replace(R.id.fragment_container,ProductFragment())
                        hide(this@WishlistFragment)
                        productViewModel.selectedProduct.value=product
                        if(favoriteViewModel.calledFrom=="Main"){
                            add<ProductFragment>(R.id.fragment_container)
                        }else if(favoriteViewModel.calledFrom=="Account"){
                            add<ProductFragment>(R.id.account_fragment_container)
                        }
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                    }
                }
            }
        })

        adapter.setWishListListener(object :WishlistListener{
            override fun addToCart(position: Int) {
                GlobalScope.launch {
                    val job=launch (Dispatchers.IO){
                        val product=favoriteViewModel.favoriteItems.value?.get(position)
                        if(product!=null){
                            val selectedProduct= SelectedProduct(product.productId,product.title,product.brand,product.thumbnail,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,1,product.originalPrice,product.priceAfterDiscount)
                            cartViewModel.addToCart(selectedProduct)
                        }
                    }
                    job.join()
                }
                Snackbar.make(recyclerView,"Added to the Cart",Snackbar.LENGTH_LONG)
                    .show()
            }

            override fun removeItem(position: Int) {
                val product=favoriteViewModel.favoriteItems.value?.get(position)
                println("The clicked product is $product")
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        if(product!=null){
                            favoriteViewModel.deleteFromFavorites(product.productId)
                            productViewModel.removeFavorite(product.productId)
                        }
                    }
                    job.join()
                    Snackbar.make(recyclerView,"Removed from WishList", Snackbar.LENGTH_LONG)
                        .setAction("UNDO"){
                            undoDelete(product)
                        }
                        .show()
                }
            }

        })

        GlobalScope.launch {
            val job=launch (Dispatchers.IO){
                favoriteViewModel.getWishlistItems()
                //adapter.setData(favoriteViewModel.getWishlistItems())
            }
            job.join()
        }

        manager = if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager(context,2)
        } else {
            //mRecycler.setLayoutManager(GridLayoutManager(mContext, 4))
            GridLayoutManager(context,4)
        }

        //manager= GridLayoutManager(context,2)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=manager

        favoriteViewModel.favoriteItems.observe(viewLifecycleOwner, Observer {

            if(favoriteViewModel.favoriteItems.value?.isEmpty()==true){
                empty.visibility=View.VISIBLE
                scroll.visibility=View.GONE
            }else{
                scroll.visibility=View.VISIBLE
                empty.visibility=View.GONE
                favoriteViewModel.favoriteItems.value?.let { it1 -> adapter.setData(it1) }
                //adapter.notifyDataSetChanged()
            }

            //adapter.setData(favoriteViewModel.favoriteItems.value!!)
            //adapter.notifyDataSetChanged()
        })

    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden){
            if(favoriteViewModel.calledFrom=="Main"){
                (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }else if(favoriteViewModel.calledFrom=="Account"){
                (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            (activity as AppCompatActivity)?.supportActionBar?.show()
        }
        super.onHiddenChanged(hidden)
    }

    private fun undoDelete(product: FavoriteProduct?) {
        GlobalScope.launch {
            val job=launch(Dispatchers.IO) {
                if(product!=null){
                    favoriteViewModel.addToFavorites(product)
                    productViewModel.markAsFavorite(product.productId)
                }
            }
            job.join()
        }
    }

}