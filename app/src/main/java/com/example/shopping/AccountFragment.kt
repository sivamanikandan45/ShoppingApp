package com.example.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment
import androidx.lifecycle.lifecycleScope
import com.example.shopping.viewmodel.CartViewModel
import com.example.shopping.viewmodel.FavoriteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountFragment : Fragment() {


    private val favoriteViewModel:FavoriteViewModel by activityViewModels()
    private val cartViewModel:CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            val job=launch (Dispatchers.IO){
                val amount=cartViewModel.getCartItemCount()
                //adapter.setData(favoriteViewModel.getWishlistItems())
            }
            job.join()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            show()
            title="Account"
            setDisplayHomeAsUpEnabled(false)
        }
        val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val currentUserId=sharePreferences?.getInt("userId",-1)
        if (currentUserId != null) {
            favoriteViewModel.setUserId(currentUserId)
        }


        val loginStatusFalseScreen=view.findViewById<ConstraintLayout>(R.id.login_skipped)
        val optionMenu=view.findViewById<LinearLayout>(R.id.account_options)

        val loginSkipped=sharePreferences?.getBoolean("login_skipped",false)
        val loginStatus=sharePreferences?.getBoolean("login_status",false)
        val loginButton=view.findViewById<Button>(R.id.login)
        loginButton.setOnClickListener {
            println("login button clicked")
            if(loginSkipped!! || !loginStatus!!){
                with(sharePreferences?.edit()){
                    this?.putBoolean("login_skipped",false)
                    this?.apply()
                }
                val intent= Intent(requireContext(),MainActivity::class.java)
                intent.putExtra("fragment_from","account")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }

        println("login status is $loginStatus")
        println("login  skioped status is $loginSkipped")
        if(loginSkipped!! || !loginStatus!!){
            loginStatusFalseScreen.visibility=View.VISIBLE
            optionMenu.visibility=View.GONE
        }else{
            loginStatusFalseScreen.visibility=View.GONE
            optionMenu.visibility=View.VISIBLE
        }
        /*(activity as AppCompatActivity).supportActionBar?.title="Account"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)*/
        val savedAddressOption=view.findViewById<ConstraintLayout>(R.id.saved_address)
        val wishlistButton=view.findViewById<ConstraintLayout>(R.id.show_wishlist)
        val myOrdersBtn=view.findViewById<ConstraintLayout>(R.id.my_orders)
        val logoutButton=view.findViewById<ConstraintLayout>(R.id.logout_button)

        logoutButton.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setTitle("Alert")
                .setMessage("You will lose the current cart items by this action!!")
                .setPositiveButton("Logout"){_,_ ->
                    lifecycleScope.launch(Dispatchers.IO){
                        val job=launch {
                            cartViewModel.clearCartItems()
                            withContext(Dispatchers.Main){
                                val sharePreferences=activity?.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
                                with(sharePreferences?.edit()){
                                    this?.putBoolean("login_skipped",false)
                                    this?.putBoolean("login_status",false)
                                    this?.putInt("userId",-1)
                                    this?.apply()
                                }
                                val intent= Intent(requireContext(),MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                        }
                        job.join()
                    }
                }
                .setNegativeButton("CANCEL"){_,_ ->

                }.show()

        }

        wishlistButton.setOnClickListener {
            favoriteViewModel.calledFrom="Account"
            goToSelectedPage("wishlist")
        }

        savedAddressOption.setOnClickListener {
            goToSelectedPage("savedAddress")
        }

        myOrdersBtn.setOnClickListener {
            goToSelectedPage("myOrders")
        }

    }

    private fun goToSelectedPage(fragmentName:String) {
        val intent=Intent(requireContext(),AccountActivity::class.java)
        intent.putExtra("frag_name",fragmentName)
        startActivity(intent)
        /*parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.fragment_container, fragment)
        }*/
    }
}