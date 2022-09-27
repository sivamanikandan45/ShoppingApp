package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.viewmodel.FavoriteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: WishlistAdapter
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()

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

        recyclerView=view.findViewById(R.id.wishlist_recyclerView)
        adapter= WishlistAdapter()
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
            adapter.setData(favoriteViewModel.favoriteItems.value!!)
            adapter.notifyDataSetChanged()
        })

    }

}