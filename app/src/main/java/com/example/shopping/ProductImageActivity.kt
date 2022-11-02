package com.example.shopping


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ProductImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_image)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val viewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        val colorDrawable = ColorDrawable(Color.parseColor("#212121"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        if (Build.VERSION.SDK_INT >= 21) {
            val window= this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //window.setStatusBarColor(this.resources.getColor(R.color.colorPrimaryDark))
            window.statusBarColor=Color.parseColor("#212121")
            window.navigationBarColor=Color.parseColor("#212121")
        }

        var list: MutableList<String> = mutableListOf<String>()
        GlobalScope.launch {
            val job=launch (Dispatchers.IO){
                println("Got the following values from intent")
                val productId=intent.getIntExtra("productId",0)
                list=viewModel.getImageUrlList(productId)
                // list= product?.productId?.let { productViewModel.getImageUrlList(it) }!!
            }
            job.join()
            withContext(Dispatchers.Main){
                val carousel=findViewById<ViewPager2>(R.id.product_image_viewpager)
                val carouselAdapter=ProductImageCarouselAdapter(list)
                carouselAdapter.setOnItemClickListener(object :ItemClickListener{
                    override fun onItemClick(position: Int) {

                    }
                })
                carousel.adapter = carouselAdapter
                val currentPos=intent.getIntExtra("currentPosition",0)
                carousel.currentItem = currentPos
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