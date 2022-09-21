package com.example.shopping

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.SelectedProduct
import com.example.shopping.viewmodel.CartViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat

class CartFragment : Fragment() {
private lateinit var recyclerView: RecyclerView
private lateinit var manager:LinearLayoutManager
private lateinit var adapter: CartAdapter
private val cartViewModel:CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Cart"

        /*val toolbar= (activity as AppCompatActivity).supportActionBar
        val badgeDrawable = BadgeDrawable.create(requireContext()).apply {
            isVisible = true
            backgroundColor = Color.RED
            number =11
        }
        BadgeUtils.attachBadgeDrawable(badgeDrawable, toolbar, R.menu.cart_menu)*/

        /*cartViewModel.cartItems.observe(this, Observer {
            println("Value changed")
            if(cartViewModel.noOfItem.value!=0){
                val badgeDrawable.getOrCreateBadge(R.id.cart)
                badgeDrawable.isVisible=true
                badgeDrawable.number=cartViewModel.noOfItem.value!!
                badgeDrawable.backgroundColor=Color.parseColor("#b00020")
                badgeDrawable.badgeTextColor=Color.WHITE
            }
        })*/

        /*println("got cart as ${cartViewModel.cartItems.value}")
        println("got cart size as ${cartViewModel.cartItems.value?.size}")*/

        /*if(cartViewModel.cartItems.value==null || cartViewModel?.cartItems.value!!.isEmpty()){
            val tv1=view.findViewById<TextView>(R.id.empty_label)
            tv1.visibility=View.VISIBLE
            *//*recyclerView=view.findViewById(R.id.cart_recycler_view)
            recyclerView.visibility=View.GONE*//*
        }else{*/
            val tv1=view.findViewById<TextView>(R.id.empty_label)
            tv1.visibility=View.GONE
            val totalAmountTextView=view.findViewById<TextView>(R.id.total_amount_value)
            val totalAmountBeforeDiscount=view.findViewById<TextView>(R.id.original_price)
            val offerTextView=view.findViewById<TextView>(R.id.discount_amount)
            //totalAmountTextView.text="$"+cartViewModel.cartAmount.value.toString()
            /*val tv2=view.findViewById<TextView>(R.id.empty_label_11)
            tv2.visibility=View.VISIBLE*/

            recyclerView=view.findViewById(R.id.cart_recycler_view)
            adapter= CartAdapter()
            manager= LinearLayoutManager(context)

        val itemTOuchHelperCallBack = object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val product=cartViewModel.cartItems.value?.get(position)
                AlertDialog.Builder(requireActivity())
                    .setTitle("Are you sure?")
                    .setMessage("1 Item will be removed from the Cart")
                    .setPositiveButton("OK"){ _,_ ->
                        removeItemFromCart(product)
                    }
                    .setNegativeButton("CANCEL"){_,_ ->
                        adapter.notifyDataSetChanged()
                    }
                    .show()
            }

        }

        val itemTouch=ItemTouchHelper(itemTOuchHelperCallBack)
        itemTouch.attachToRecyclerView(recyclerView)

            GlobalScope.launch {
                val job=launch {
                    adapter.setData(cartViewModel.getCartItems())
                    val priceAfterDiscount=cartViewModel.getCartAmountAfterDiscount()
                    val priceBeforeDiscount=cartViewModel.getCartAmountBeforeDiscount()
                    totalAmountTextView.text=priceAfterDiscount.toString()
                    totalAmountBeforeDiscount.text=priceBeforeDiscount.toString()
                    var discountAmount=priceBeforeDiscount-priceAfterDiscount
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.UP
                    discountAmount = df.format(discountAmount).toDouble()
                    offerTextView.text="-$"+discountAmount.toString()
                }
                job.join()
                withContext(Dispatchers.Main){
                    /*val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL *//*or LinearLayoutManager.HORIZONTAL*//*) }
                    divider?.dividerInsetStart=375
                    recyclerView.addItemDecoration(divider!!)*/
                    //totalAmountTextView.text="$"+cartViewModel.cartAmount.value.toString()
                    recyclerView.adapter=adapter
                    recyclerView.layoutManager=manager
                }

            //}
            /*recyclerView.setHasFixedSize(true)
            recyclerView.adapter=adapter
            recyclerView.layoutManager=manager*/

        }

        cartViewModel.cartItems.observe(viewLifecycleOwner, Observer {
            adapter.setData(cartViewModel.cartItems.value!!)
            adapter.notifyDataSetChanged()
        })

        cartViewModel.cartAmountAfterDiscount.observe(viewLifecycleOwner, Observer {
            totalAmountTextView.text="$"+it.toString()
            GlobalScope.launch{
                val job=launch(Dispatchers.IO) {
                    val priceAfterDiscount=it
                    val priceBeforeDiscount=cartViewModel.getCartAmountBeforeDiscount()
                    withContext(Dispatchers.Main){
                        var discountAmount=(priceBeforeDiscount-priceAfterDiscount)
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.DOWN
                        discountAmount = df.format(discountAmount).toDouble()
                        offerTextView.text="-$"+discountAmount.toString()
                    }
                }
                job.join()
            }
        })

        /*cartViewModel.discountAmount.observe(viewLifecycleOwner, Observer {
            offerTextView.text="-$"+it.toString()
            *//*val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            totalAmountBeforeDiscount.text=df.format(it).toDouble().toString()*//*
        })*/

        cartViewModel.cartAmountBeforeDiscount.observe(viewLifecycleOwner, Observer {
            totalAmountBeforeDiscount.text="$"+it.toString()
        })
    }

    private fun removeItemFromCart(product: SelectedProduct?) {
        GlobalScope.launch {
            val job = launch(Dispatchers.IO) {
                cartViewModel.deleteProduct(product?.productId)
                println("$product is deleted")
            }
            job.join()
            Snackbar.make(recyclerView, "1 Item Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    if (product != null) {
                        GlobalScope.launch {
                            val job = launch(Dispatchers.IO) {
                                cartViewModel.addToCart(product)
                            }
                            job.join()
                        }
                    }
                }.show()
        }
    }

}