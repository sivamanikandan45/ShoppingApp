package com.example.shopping.wishlist.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.*
import com.example.shopping.wishlist.model.FavoriteProduct
import com.example.shopping.cart.model.SelectedProductEntity
import com.example.shopping.cart.ui.viewmodel.CartViewModel
import com.example.shopping.product.ui.CategoryActivity
import com.example.shopping.wishlist.ui.viewmodel.FavoriteViewModel
import com.example.shopping.product.ui.viewmodel.ProductViewModel
import com.example.shopping.product.ui.viewmodel.RecentlyViewedViewModel
import com.example.shopping.util.ItemClickListener
import com.example.shopping.wishlist.ui.adapter.WishlistAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: WishlistAdapter
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private val productViewModel: ProductViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val recentlyViewedViewModel: RecentlyViewedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val job=launch (Dispatchers.IO){
                val amount=cartViewModel.getCartItemCount()
                favoriteViewModel.getWishlistItems()
                //adapter.setData(favoriteViewModel.getWishlistItems())
            }
            job.join()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            show()
            title="Wishlist"
        }
        val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharePreferences?.getInt("userId",-1)
        if (currentUserId != null) {
            favoriteViewModel.setUserId(currentUserId)
        }

        if(favoriteViewModel.calledFrom=="Main"){
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }else if(favoriteViewModel.calledFrom=="Account"){
            println("called from acc")
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val empty=view.findViewById<ConstraintLayout>(R.id.empty_page)
        val scroll=view.findViewById<RecyclerView>(R.id.wishlist_recyclerView)
        val loginStatusFalseScreen=view.findViewById<ConstraintLayout>(R.id.login_skipped)

        recyclerView=view.findViewById(R.id.wishlist_recyclerView)
        adapter= WishlistAdapter()
        adapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedProduct=favoriteViewModel.favoriteItems.value?.get(position)
                val intent= Intent(context, CategoryActivity::class.java)
                intent.putExtra("fragment_name","product")
                intent.putExtra("selected_product_id",selectedProduct?.productId)
                startActivity(intent)
                /*parentFragmentManager.commit {
                    addToBackStack(null)
                    val selectedProduct=favoriteViewModel.favoriteItems.value?.get(position)
                    if(selectedProduct!=null){


                        *//*val product=Product(selectedProduct.productId,selectedProduct.title,selectedProduct.description,selectedProduct.originalPrice,selectedProduct.discountPercentage,selectedProduct.priceAfterDiscount,selectedProduct.rating,selectedProduct.stock,selectedProduct.brand,selectedProduct.category,selectedProduct.thumbnail,true)
                        productViewModel.selectedProduct.value=product
                        //replace(R.id.fragment_container,ProductFragment())
                        hide(this@WishlistFragment)
                        productViewModel.selectedProduct.value=product
                        if(favoriteViewModel.calledFrom=="Main"){
                            add<ProductFragment>(R.id.fragment_container)
                        }else if(favoriteViewModel.calledFrom=="Account"){
                            add<ProductFragment>(R.id.account_fragment_container)
                        }
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)*//*

                    }
                }*/
            }
        })

        adapter.setWishListListener(object : WishlistListener {
            override fun moveToCart(position: Int) {
                val product=favoriteViewModel.favoriteItems.value?.get(position)
                /*val dialogFragment=QuantityDialogFragment(this@WishlistFragment)
                dialogFragment.show(parentFragmentManager,"")*/
                if(product?.let { cartViewModel.isProductInCart(it.productId) } == true){
                    val count=cartViewModel.getProductCount(product.productId)
                    val selectedProduct=cartViewModel.selectedProduct(product.productId)
                    if (selectedProduct != null) {
                        if(count!=10){
                            cartViewModel.updateQuantity(selectedProduct,selectedProduct.quantity+1)
                            favoriteViewModel.deleteFromFavorites(selectedProduct.productId)
                            Snackbar.make(recyclerView,"Moved to the Cart",Snackbar.LENGTH_LONG)
                                .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation_view))
                                .show()
                        }else{
                            Snackbar.make(recyclerView,"Maximum limit reached for the particular product",Snackbar.LENGTH_LONG)
                                .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation_view))
                                .show()
                        }
                    }
                }else{
                    if(product!=null){
                        val selectedProductEntity = SelectedProductEntity(product.productId,product.originalPrice,product.discountPercentage,product.priceAfterDiscount,1,product.originalPrice,product.priceAfterDiscount)
                        cartViewModel.addToCart(selectedProductEntity)
                        favoriteViewModel.deleteFromFavorites(selectedProductEntity.productId)
                    }
                    Snackbar.make(recyclerView,"Moved to the Cart",Snackbar.LENGTH_LONG)
                        .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation_view))
                        .show()
                }
            }

            override fun removeItem(position: Int) {
                val product=favoriteViewModel.favoriteItems.value?.get(position)
                var id=0
                if(product!=null){
                    favoriteViewModel.deleteFromFavorites(product.productId)
                    if (currentUserId != null) {
                        lifecycleScope.launch(Dispatchers.IO){
                            id=favoriteViewModel.getIdOfFavorite(product.productId,currentUserId)
                        }
                    }
                    //recentlyViewedViewModel.updateFavoriteStatus(false,product.productId)
                }
                Snackbar.make(recyclerView,"Removed from WishList", Snackbar.LENGTH_LONG)
                        .setAction("UNDO"){
                            val favoriteProduct=
                                currentUserId?.let { it1 -> product?.productId?.let { it2 -> FavoriteProduct(id, it2, it1) } }
                            undoDelete(favoriteProduct)
                        }
                        .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation_view))
                        .show()
            }

        })

        lifecycleScope.launch {
            val job=launch (Dispatchers.IO){
                favoriteViewModel.getWishlistItems()
                //adapter.setData(favoriteViewModel.getWishlistItems())
            }
            job.join()
        }

        manager = if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager(context,2)
        } else {
            GridLayoutManager(context,4)
        }

        //manager= GridLayoutManager(context,2)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=manager


        //val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val loginSkipped=sharePreferences?.getBoolean("login_skipped",false)
        val loginStatus=sharePreferences?.getBoolean("login_status",false)

        val loginButton=view.findViewById<Button>(R.id.login)
        loginButton.setOnClickListener {
            if(loginSkipped!! || !loginStatus!!){
                with(sharePreferences.edit()){
                    this?.putBoolean("login_skipped",false)
                    this?.apply()
                }
                val intent= Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("fragment_from","wishlist")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        if(loginSkipped!! || !loginStatus!!){
            loginStatusFalseScreen.visibility=View.VISIBLE
            empty.visibility=View.GONE
            scroll.visibility=View.GONE
        }else{
            favoriteViewModel.favoriteItems.observe(viewLifecycleOwner, Observer {
                if(favoriteViewModel.favoriteItems.value?.isEmpty()==true){
                    loginStatusFalseScreen.visibility=View.GONE
                    empty.visibility=View.VISIBLE
                    scroll.visibility=View.GONE
                }else{
                    loginStatusFalseScreen.visibility=View.GONE
                    scroll.visibility=View.VISIBLE
                    empty.visibility=View.GONE
                    favoriteViewModel.favoriteItems.value?.let { it1 -> adapter.setData(it1) }
                }
            })
        }



    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden){
            if(favoriteViewModel.calledFrom=="Main"){
                (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }else if(favoriteViewModel.calledFrom=="Account"){
                (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            (activity as AppCompatActivity).supportActionBar?.show()
        }
        super.onHiddenChanged(hidden)
    }

    private fun undoDelete(product: FavoriteProduct?) {
        if(product!=null){
            favoriteViewModel.addToFavorites(product)
            //recentlyViewedViewModel.updateFavoriteStatus(true,product.productId)
        }
    }

}