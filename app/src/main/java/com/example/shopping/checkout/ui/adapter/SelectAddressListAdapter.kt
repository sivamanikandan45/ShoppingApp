package com.example.shopping.checkout.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.util.ItemClickListener
import com.example.shopping.R
import com.example.shopping.address.model.Address

class SelectAddressListAdapter:RecyclerView.Adapter<SelectAddressListAdapter.ViewHolder>(){
    //private lateinit var list:List<Address>
    private var list= listOf<Address>()
   private lateinit var listener: ItemClickListener
    var selectedPosition=0

    /*fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }*/

    fun setData(list:List<Address>){
        this.list=list
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }

    fun updateSelectedPosition(position: Int){
        selectedPosition=position
        //notifyItemChanged(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val nameTextView:TextView
        val addressTextView:TextView
        val radioBtn:RadioButton
        val phnTextView:TextView
        init{
            nameTextView=view.findViewById(R.id.cust_name)
            addressTextView=view.findViewById(R.id.address)
            radioBtn=view.findViewById(R.id.radioButton)
            phnTextView=view.findViewById(R.id.phn_no)
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
                /*selectedPosition=adapterPosition
                notifyDataSetChanged()*/
                //updateSelectedPosition(adapterPosition) ----->// changed here
                println("The selected Position is $selectedPosition")
            }
        }

        fun bind(address: Address) {
            nameTextView.text=address.name
            val addressText="${address.street}, ${address.area}, ${address.city}, ${address.state} - ${address.pinCode}"
            addressTextView.text=addressText
            phnTextView.text=address.phone
            radioBtn.isChecked=(selectedPosition==adapterPosition)
            radioBtn.setOnClickListener {
                updateSelectedPosition(adapterPosition)
            }
            /*radioBtn.setOnCheckedChangeListener { compoundButton, b ->
                if(b){
                    //updateSelectedPosition(adapterPosition)
                    selectedPosition=adapterPosition
                    println("Seleceted position is $selectedPosition")
                }
            }*/

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_address,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}