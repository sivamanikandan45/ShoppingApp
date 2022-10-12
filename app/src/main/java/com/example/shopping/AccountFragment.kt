package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.commit

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title="Account"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val savedAddressOption=view.findViewById<ConstraintLayout>(R.id.saved_address)
        val wishlistButton=view.findViewById<ConstraintLayout>(R.id.show_wishlist)
        val myOrdersBtn=view.findViewById<ConstraintLayout>(R.id.my_orders)

        wishlistButton.setOnClickListener {
            replaceFragment(WishlistFragment())
        }

        savedAddressOption.setOnClickListener {
            replaceFragment(SavedAddressFragment())
        }

        myOrdersBtn.setOnClickListener {
            replaceFragment(MyOrdersFragment())
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.fragment_container, fragment)
        }
    }
}