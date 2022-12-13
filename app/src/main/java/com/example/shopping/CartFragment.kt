package com.example.shopping

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.SelectedProduct
import com.example.shopping.util.SwipeToDeleteCallback
import com.example.shopping.viewmodel.CartViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
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
    private var itemTouch:ItemTouchHelper?=null
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
        (activity as AppCompatActivity).supportActionBar?.apply {
            show()
            title="Cart"
            setDisplayHomeAsUpEnabled(false)
        }

        /*val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true ) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)*/
        /*val bottomSheet=view.findViewById<FrameLayout>(R.id.bottom_sheet)
        val behavior=BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight=200
            state=BottomSheetBehavior.STATE_COLLAPSED
        }*/

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
        val empty=view.findViewById<ConstraintLayout>(R.id.empty_page)
        val scroll=view.findViewById<NestedScrollView>(R.id.scroll)
        println("Cart items while loading ${cartViewModel.cartItems.value}")
        /*if(cartViewModel.cartItems.value?.isEmpty()==true){
            empty.visibility=View.VISIBLE
            scroll.visibility=View.GONE
        }else{
            scroll.visibility=View.VISIBLE
            empty.visibility=View.GONE
        }*/
        val tv1=view.findViewById<TextView>(R.id.empty_label)
        tv1.visibility=View.GONE
        val totalAmountTextView=view.findViewById<TextView>(R.id.total_amount_value)
        val finalTotalAmountTextView=view.findViewById<TextView>(R.id.final_total_amt)
        val totalAmountBeforeDiscount=view.findViewById<TextView>(R.id.original_price)
        val offerTextView=view.findViewById<TextView>(R.id.discount_amount)
        val savingInfo=view.findViewById<TextView>(R.id.saving_info)
        val placeOrderButton=view.findViewById<Button>(R.id.place_order_btn)

        placeOrderButton.setOnClickListener {
            val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
            val loginSkipped=sharePreferences?.getBoolean("login_skipped",false)
            val loginStatus=sharePreferences?.getBoolean("login_status",false)
            println("login status is $loginStatus")
            println("login  skioped status is $loginSkipped")
            if(loginSkipped!! || !loginStatus!!){
                lifecycleScope.launch(Dispatchers.Main){
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Login Required")
                        .setMessage("Log in for the best experience")
                        .setPositiveButton("Login") { _, _ ->
                            val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                            with(sharePreferences?.edit()){
                                this?.putBoolean("login_skipped",false)
                                //this?.putBoolean("login_status",false)
                                this?.apply()
                            }
                            val intent= Intent(requireContext(),MainActivity::class.java)
                            intent.putExtra("fragment_from","cart")
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                        .show()
                }
            }else{
                val intent=Intent(requireContext(),CheckoutActivity::class.java)
                startActivity(intent)
            }
        }
        //totalAmountTextView.text="$"+cartViewModel.cartAmount.value.toString()
        /*val tv2=view.findViewById<TextView>(R.id.empty_label_11)
        tv2.visibility=View.VISIBLE*/

        recyclerView=view.findViewById(R.id.cart_recycler_view)
        adapter= CartAdapter()
        adapter.setOnQuantityClickListener(object :QuantityButtonListener{
            override fun onIncreaseClicked(adapterPosition: Int) {
                println("Increase clicked at $adapterPosition")
                println("increased item is ${cartViewModel.cartItems.value?.get(adapterPosition)?.productName}")
                val product=cartViewModel.cartItems.value?.get(adapterPosition)
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        cartViewModel.updateQuantity(product!!,product.quantity)
                    }
                    job.join()
                }
            }

            override fun onDecreaseClicked(adapterPosition:Int) {
                println("Decrease clicked")
                val product=cartViewModel.cartItems.value?.get(adapterPosition)
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        cartViewModel.updateQuantity(product!!,product.quantity)
                    }
                    job.join()
                }
            }
        })
        manager= LinearLayoutManager(context)

        val callback=object :SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val product=cartViewModel.cartItems.value?.get(position)
                if(direction==ItemTouchHelper.LEFT) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Remove Item")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton("REMOVE") { _, _ ->
                            removeItemFromCart(product)
                        }
                        .setNegativeButton("CANCEL") { _, _ ->
                            //adapter.notifyItemChanged(position)
                        }
                        .show()
                    adapter.notifyItemChanged(position)
                }
            }

        }

        val itemTouchHelperCallBack = object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)
            val background = ColorDrawable()
            val clearPaint = Paint()
            init {
                clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
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
                if(direction==ItemTouchHelper.LEFT){
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Remove Item")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton("REMOVE"){ _,_ ->
                            removeItemFromCart(product)
                        }
                        .setNegativeButton("CANCEL"){_,_ ->
                            //adapter.notifyItemChanged(position)
                        }
                        .show()

                    /*viewHolder.itemView
                        .animate()
                        .translationX(0f)
                        .withEndAction {
                            itemTouch?.attachToRecyclerView(null)
                            itemTouch?.attachToRecyclerView(recyclerView)
                        }
                        .start()*/
                    /*itemTouch?.attachToRecyclerView(null)
                    itemTouch?.attachToRecyclerView(recyclerView)*/
                    adapter.notifyItemChanged(position)

                }else if(direction==ItemTouchHelper.RIGHT){
                    itemTouch?.attachToRecyclerView(null)
                    itemTouch?.attachToRecyclerView(recyclerView)
                }

            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView=viewHolder.itemView
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
                val iconBottom = iconTop + icon!!.intrinsicHeight
                val isCancelled = dX == 0f && !isCurrentlyActive
                if(isCancelled){
                    clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }//else if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE&&isCurrentlyActive){
                    when{
                        dX<0->{
                            background.color=Color.parseColor("#b00020")
                            background.setBounds(itemView.right+dX.toInt(),itemView.top,itemView.right,itemView.bottom)
                            background.draw(c)
                            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                            val iconRight = itemView.right - iconMargin
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            icon.draw(c)
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        }
                        dX>0->{
                            val newDx=dX/20
                            background.color=Color.LTGRAY
                            background.setBounds(itemView.left,itemView.top, newDx.toInt(),itemView.bottom)
                            background.draw(c)
                            super.onChildDraw(c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive)
                        }
                    }
                //}
            }

            private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
                c.drawRect(left, top, right, bottom, clearPaint)
            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 0.7f
            }

        }

        itemTouch=ItemTouchHelper(itemTouchHelperCallBack)
        //itemTouch.attachToRecyclerView(null)
        itemTouch?.attachToRecyclerView(recyclerView)

        GlobalScope.launch {
            val job=launch {
                adapter.setData(cartViewModel.getCartItems())
                val priceAfterDiscount=cartViewModel.getCartAmountAfterDiscount()
                val priceBeforeDiscount=cartViewModel.getCartAmountBeforeDiscount()
                withContext(Dispatchers.Main){
                    val decimalFormat = DecimalFormat("#.##")
                    decimalFormat.roundingMode = RoundingMode.UP
                    val priceAfterDiscountRounded= decimalFormat.format(priceAfterDiscount).toDouble()
                    totalAmountTextView.text="₹$priceAfterDiscountRounded"
                    finalTotalAmountTextView.text="₹$priceAfterDiscountRounded"
                    totalAmountBeforeDiscount.text=priceBeforeDiscount.toString()
                    var discountAmount=priceBeforeDiscount-priceAfterDiscount
                    discountAmount = decimalFormat.format(discountAmount).toDouble()
                    offerTextView.text="-₹$discountAmount"
                    savingInfo.text="You will save ₹$discountAmount on this order"
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                val divider = context?.let { MaterialDividerItemDecoration(it,LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL) }
                /*divider?.dividerInsetStart=396*/
                divider?.isLastItemDecorated = false
                divider?.let { recyclerView.addItemDecoration(it) }
                //totalAmountTextView.text="$"+cartViewModel.cartAmount.value.toString()
                recyclerView.adapter=adapter
                //ViewCompat.setNestedScrollingEnabled(recyclerView, false);
                recyclerView.layoutManager=manager
            }

            //}
            /*recyclerView.setHasFixedSize(true)
            recyclerView.adapter=adapter
            recyclerView.layoutManager=manager*/

        }

        cartViewModel.cartItems.observe(viewLifecycleOwner, Observer {
            if(cartViewModel.cartItems.value?.isEmpty()==true){
                empty.visibility=View.VISIBLE
                scroll.visibility=View.GONE
            }else{
                scroll.visibility=View.VISIBLE
                empty.visibility=View.GONE
                adapter.setData(cartViewModel.cartItems.value!!)
                //adapter.notifyDataSetChanged()
            }
        })

        cartViewModel.cartAmountAfterDiscount.observe(viewLifecycleOwner, Observer {
            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.roundingMode = RoundingMode.UP
            val cartAmountAfterDiscountRounded = decimalFormat.format(it).toDouble()
            val withSymbol="₹$cartAmountAfterDiscountRounded"
            totalAmountTextView.text=withSymbol
            finalTotalAmountTextView.text=withSymbol

            GlobalScope.launch{
                val job=launch(Dispatchers.IO) {
                    val priceAfterDiscount=it
                    val priceBeforeDiscount=cartViewModel.getCartAmountBeforeDiscount()
                    withContext(Dispatchers.Main){
                        var discountAmount=(priceBeforeDiscount-priceAfterDiscount)
                        val newDecimalFormat = DecimalFormat("#.##")
                        newDecimalFormat.roundingMode = RoundingMode.DOWN
                        discountAmount = newDecimalFormat.format(discountAmount).toDouble()
                        val offer="-₹$discountAmount"
                        offerTextView.text=offer
                        savingInfo.text="You will save ₹$discountAmount on this order"
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
            val formatted="₹$it"
            totalAmountBeforeDiscount.text=formatted
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
                            val addingJob = launch(Dispatchers.IO) {
                                cartViewModel.addToCart(product)
                            }
                            addingJob.join()
                        }
                    }
                }.setAnchorView(requireActivity().findViewById(R.id.bottom_navigation_view))
                .show()
        }
    }

}