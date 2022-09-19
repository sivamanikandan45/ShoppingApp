package com.example.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.shopping.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sort_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productViewModel:ProductViewModel by activityViewModels()
        val alphabetSort=view.findViewById<ConstraintLayout>(R.id.sort_by_alpha)
        val ratingSort=view.findViewById<ConstraintLayout>(R.id.sort_by_rating)
        val priceHighToLowSort=view.findViewById<ConstraintLayout>(R.id.sort_by_price_htl)
        val priceLowToHighSort=view.findViewById<ConstraintLayout>(R.id.sort_by_price_lth)

        alphabetSort.setOnClickListener {
            productViewModel.sortByAlphabet()
            dismiss()
        }

        ratingSort.setOnClickListener {
            productViewModel.sortByARating()
            dismiss()
        }

        priceHighToLowSort.setOnClickListener {
            productViewModel.sortByPriceHghToLow()
            dismiss()
        }

        priceLowToHighSort.setOnClickListener {
            productViewModel.sortByPriceLowToHigh()
            dismiss()
        }

    }

}