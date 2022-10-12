package com.example.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Order

class OrderAdapter:RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var list:List<Order>
    private lateinit var listener: ItemClickListener

    fun setData(list:List<Order>){
        this.list=list
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val orderIdTextView:TextView
        val itemCountTextView:TextView
        val orderDateTextView:TextView
        val billAmountTextView:TextView
        val addressTextView:TextView

        init {
            orderIdTextView=view.findViewById(R.id.order_id)
            itemCountTextView=view.findViewById(R.id.order_item_count)
            orderDateTextView=view.findViewById(R.id.order_date)
            billAmountTextView=view.findViewById(R.id.order_bill_amount)
            addressTextView=view.findViewById(R.id.address)
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(order: Order) {
            orderIdTextView.text="#${order.orderId}"
            itemCountTextView.text="${order.itemCount} Item"
            orderDateTextView.text="Ordered on ${order.orderedDate}"
            billAmountTextView.text="$${order.totalAfterDiscount}"
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