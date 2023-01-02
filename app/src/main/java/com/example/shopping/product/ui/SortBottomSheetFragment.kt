package com.example.shopping.product.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import com.example.shopping.R
import com.example.shopping.product.enums.Sort
import com.example.shopping.product.ui.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheetFragment : BottomSheetDialogFragment() {
//    private val sortViewModel:SortViewModel by activityViewModels()
    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sort_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productViewModel: ProductViewModel by activityViewModels()
        val alphabetSort=view.findViewById<ConstraintLayout>(R.id.sort_by_alpha)
        val ratingSort=view.findViewById<ConstraintLayout>(R.id.sort_by_rating)
        val priceHighToLowSort=view.findViewById<ConstraintLayout>(R.id.sort_by_price_htl)
        val priceLowToHighSort=view.findViewById<ConstraintLayout>(R.id.sort_by_price_lth)

        val alphabetCheck=view.findViewById<ImageView>(R.id.alphabet_check)
        val ratingCheck=view.findViewById<ImageView>(R.id.rating_check)
        val priceHighToLowCheck=view.findViewById<ImageView>(R.id.price_high_check)
        val priceLowToHighCheck=view.findViewById<ImageView>(R.id.price_low_check)

        when(productViewModel.selectedSort){
            Sort.NONE->{
                alphabetCheck.visibility=View.GONE
                ratingCheck.visibility=View.GONE
                priceHighToLowCheck.visibility=View.GONE
                priceLowToHighCheck.visibility=View.GONE
            }
            Sort.ALPHA->{
                alphabetCheck.visibility=View.VISIBLE
                ratingCheck.visibility=View.GONE
                priceHighToLowCheck.visibility=View.GONE
                priceLowToHighCheck.visibility=View.GONE
            }
            Sort.RATING->{
                alphabetCheck.visibility=View.GONE
                ratingCheck.visibility=View.VISIBLE
                priceHighToLowCheck.visibility=View.GONE
                priceLowToHighCheck.visibility=View.GONE
            }
            Sort.PRICE_HIGH_TO_LOW->{
                alphabetCheck.visibility=View.GONE
                ratingCheck.visibility=View.GONE
                priceHighToLowCheck.visibility=View.VISIBLE
                priceLowToHighCheck.visibility=View.GONE
            }
            Sort.PRICE_LOW_TO_HIGH->{
                alphabetCheck.visibility=View.GONE
                ratingCheck.visibility=View.GONE
                priceHighToLowCheck.visibility=View.GONE
                priceLowToHighCheck.visibility=View.VISIBLE
            }
        }

        alphabetSort.setOnClickListener {
            productViewModel.selectedSort= Sort.ALPHA

            alphabetCheck.visibility=View.VISIBLE
            ratingCheck.visibility=View.GONE
            priceHighToLowCheck.visibility=View.GONE
            priceLowToHighCheck.visibility=View.GONE

            productViewModel.sortByAlphabet()
            dismiss()
        }

        ratingSort.setOnClickListener {

            productViewModel.selectedSort= Sort.RATING

            alphabetCheck.visibility=View.GONE
            ratingCheck.visibility=View.VISIBLE
            priceHighToLowCheck.visibility=View.GONE
            priceLowToHighCheck.visibility=View.GONE

            productViewModel.sortByARating()
            dismiss()
        }

        priceHighToLowSort.setOnClickListener {
            productViewModel.selectedSort= Sort.PRICE_HIGH_TO_LOW

            alphabetCheck.visibility=View.GONE
            ratingCheck.visibility=View.GONE
            priceHighToLowCheck.visibility=View.VISIBLE
            priceLowToHighCheck.visibility=View.GONE

            productViewModel.sortByPriceHghToLow()
            dismiss()
        }

        priceLowToHighSort.setOnClickListener {

            productViewModel.selectedSort= Sort.PRICE_LOW_TO_HIGH

            alphabetCheck.visibility=View.GONE
            ratingCheck.visibility=View.GONE
            priceHighToLowCheck.visibility=View.GONE
            priceLowToHighCheck.visibility=View.VISIBLE

            productViewModel.sortByPriceLowToHigh()
            dismiss()
        }

    }

}