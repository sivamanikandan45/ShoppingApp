package com.example.shopping.product.ui


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.util.ItemClickListener
import com.example.shopping.product.ui.adapter.ProductImageCarouselAdapter
import com.example.shopping.R
import com.example.shopping.product.model.CarouselImage
import com.example.shopping.product.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_image)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val viewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        val colorDrawable = ColorDrawable(Color.parseColor("#000000"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        if (Build.VERSION.SDK_INT >= 21) {
            val window= this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //window.setStatusBarColor(this.resources.getColor(R.color.colorPrimaryDark))
            window.statusBarColor=Color.parseColor("#000000")
            window.navigationBarColor=Color.parseColor("#000000")
        }

        var list: MutableList<CarouselImage> = mutableListOf<CarouselImage>()
        lifecycleScope.launch {
            val job=launch (Dispatchers.IO){
                println("Got the following values from intent")
                val productId=intent.getIntExtra("productId",0)
                list=viewModel.getImageUrlList(productId)
                // list= product?.productId?.let { productViewModel.getImageUrlList(it) }!!
            }
            job.join()
            withContext(Dispatchers.Main){
                val currentPos=intent.getIntExtra("currentPosition",0)
                val carousel=findViewById<ViewPager2>(R.id.product_image_viewpager)
                val carouselAdapter= ProductImageCarouselAdapter(list)
                carouselAdapter.setOnItemClickListener(object : ItemClickListener {
                    override fun onItemClick(position: Int) {

                    }
                })
                carousel.adapter = carouselAdapter
                carousel.setCurrentItem(currentPos,false)
                //carousel.currentItem = currentPos
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(supportFragmentManager.backStackEntryCount>0){
                    supportFragmentManager.popBackStack()
                }else{
                    onBackPressed()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}