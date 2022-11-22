package com.example.shopping

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class OnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title="Shopping"
            setDisplayHomeAsUpEnabled(false)
        }
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility=View.GONE
        val skipButton=view.findViewById<Button>(R.id.skip)
        val loginButton=view.findViewById<Button>(R.id.login)
        val registerButton=view.findViewById<Button>(R.id.register)

        loginButton.setOnClickListener {
            parentFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.fragment_container,LoginFragment())
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }
        }

        registerButton.setOnClickListener {
            parentFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.fragment_container,RegisterFragment())
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }
        }
        skipButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container,HomeFragment())
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                val sharePreferences=activity?.getSharedPreferences("shared_preferences",Context.MODE_PRIVATE)
                with(sharePreferences?.edit()){
                    this?.putBoolean("login_skipped",true)
                    this?.apply()
                }
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility=View.VISIBLE
            }
        }
    }

}