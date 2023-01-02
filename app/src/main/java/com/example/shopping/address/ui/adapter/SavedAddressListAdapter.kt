package com.example.shopping.address.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.address.ui.AddressMenuListener
import com.example.shopping.R
import com.example.shopping.address.model.Address

class SavedAddressListAdapter:RecyclerView.Adapter<SavedAddressListAdapter.ViewHolder>() {
    private lateinit var list:List<Address>
    private lateinit var addressMenuListener: AddressMenuListener

    fun setAddressMenuListener(listener: AddressMenuListener){
        this.addressMenuListener=listener
    }

    fun setData(list:List<Address>){
        this.list=list
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val nameTextView: TextView
        val addressTextView: TextView
        val phnTextView: TextView
        val menu:ImageView
        init{
            nameTextView=view.findViewById(R.id.cust_name)
            addressTextView=view.findViewById(R.id.address)
            phnTextView=view.findViewById(R.id.phn_no)
            menu=view.findViewById(R.id.more)

            menu.setOnClickListener{
                val popupMenu= PopupMenu(menu.context,menu)
                popupMenu.inflate(R.menu.address_menu)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.action_delete_address ->{
                            addressMenuListener.deleteAddress(adapterPosition)
                            true
                        }
                        R.id.action_edit_address ->{
                            addressMenuListener.editAddress(adapterPosition)
                            true
                        }
                        else->{
                            false
                        }
                    }
                }
                popupMenu.show()
            }
        }

        fun bind(address: Address) {
            nameTextView.text=address.name
            val addressText="${address.street}, ${address.area}, ${address.city}, ${address.state} - ${address.pinCode}"
            addressTextView.text=addressText
            phnTextView.text=address.phone
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_saved_address,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}