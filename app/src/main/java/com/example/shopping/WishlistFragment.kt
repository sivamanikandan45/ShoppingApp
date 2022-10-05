package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Wishlist"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

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
                        replace(R.id.fragment_container,ProductFragment())
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
                adapter.setData(favoriteViewModel.getWishlistItems())
            }
            job.join()
        }

        manager= GridLayoutManager(context,2)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=manager

        favoriteViewModel.favoriteItems.observe(viewLifecycleOwner, Observer {

            if(favoriteViewModel.favoriteItems.value?.isEmpty()==true){
                empty.visibility=View.VISIBLE
                scroll.visibility=View.GONE
            }else{
                scroll.visibility=View.VISIBLE
                empty.visibility=View.GONE
                adapter.setData(favoriteViewModel.favoriteItems.value!!)
                //adapter.notifyDataSetChanged()
            }

            //adapter.setData(favoriteViewModel.favoriteItems.value!!)
            //adapter.notifyDataSetChanged()
        })

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