package com.example.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Order
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

class OrderAdapter:RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var list:List<Order>
    private lateinit var listener: ItemClickListener
    private lateinit var loader: ImageLoader

    fun setData(list:List<Order>){
        this.list=list
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }

    fun getImageList(loader: ImageLoader){
        this.loader=loader
    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val orderIdTextView:TextView
        val itemCountTextView:TextView
        val orderDateTextView:TextView
        val billAmountTextView:TextView
        val imageView:ShapeableImageView
        //val addressTextView:TextView

        init {
            orderIdTextView=view.findViewById(R.id.order_id)
            itemCountTextView=view.findViewById(R.id.order_item_count)
            orderDateTextView=view.findViewById(R.id.order_date)
            billAmountTextView=view.findViewById(R.id.order_bill_amount)
            imageView=view.findViewById(R.id.shapeableImageView)
//            addressTextView=view.findViewById(R.id.address)
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(order: Order) {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            val caad= df.format(order.totalAfterDiscount).toDouble()

            orderIdTextView.text="Order Id: #${order.orderId}"
            itemCountTextView.text="${order.itemCount} Item"
            orderDateTextView.text="Ordered on ${order.orderedDate}"
            billAmountTextView.text="₹$caad"
            GlobalScope.launch {
                val job=launch (Dispatchers.IO){
                    val list=loader.loadImage(list[adapterPosition].orderId)
                    println("Got $list")
                }
                job.join()
            }
            //addressTextView.text=order
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_order,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}