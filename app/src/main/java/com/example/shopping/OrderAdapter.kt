package com.example.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.model.Order
import com.example.shopping.model.OrderedProduct
import com.example.shopping.util.ProductImageMemoryCache
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat

class OrderAdapter:RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var list:List<Order>
    private lateinit var listener: ItemClickListener
    private lateinit var loader: DataLoader

    fun setData(list:List<Order>){
        this.list=list
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        this.listener=listener
    }

    fun getImageList(loader: DataLoader){
        this.loader=loader
    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val orderIdTextView:TextView
        val itemCountTextView:TextView
        val orderDateTextView:TextView
        val billAmountTextView:TextView
        val imageView:ShapeableImageView
        val twoSplitImageView:CardView
        val threeSplitImageView:CardView
        val fourSplitImageView:CardView

        val firstImageInTwoSplit:ImageView
        val secondImageInTwoSplit:ImageView

        val firstImageInThreeSplit:ImageView
        val secondImageInThreeSplit:ImageView
        val thirdImageInThreeSplit:ImageView

        val firstImageInFourSplit:ImageView
        val secondImageInFourSplit:ImageView
        val thirdImageInFourSplit:ImageView
        val fourthImageInFourSplit:ImageView


        //val addressTextView:TextView

        init {
            orderIdTextView=view.findViewById(R.id.order_id)
            itemCountTextView=view.findViewById(R.id.order_item_count)
            orderDateTextView=view.findViewById(R.id.order_date)
            billAmountTextView=view.findViewById(R.id.order_bill_amount)
            imageView=view.findViewById(R.id.shapeableImageView)
            twoSplitImageView=view.findViewById(R.id.two_image)
            firstImageInTwoSplit=view.findViewById(R.id.two_image_1)
            secondImageInTwoSplit=view.findViewById(R.id.two_image_2)

            threeSplitImageView=view.findViewById(R.id.three_image)
            firstImageInThreeSplit=view.findViewById(R.id.three_image_1)
            secondImageInThreeSplit=view.findViewById(R.id.three_image_2)
            thirdImageInThreeSplit=view.findViewById(R.id.three_image_3)

            fourSplitImageView=view.findViewById(R.id.four_image)
            firstImageInFourSplit=view.findViewById(R.id.four_image_1)
            secondImageInFourSplit=view.findViewById(R.id.four_image_2)
            thirdImageInFourSplit=view.findViewById(R.id.four_image_3)
            fourthImageInFourSplit=view.findViewById(R.id.four_image_4)


//            addressTextView=view.findViewById(R.id.address)
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(order: Order) {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.UP
            val caad= df.format(order.totalAfterDiscount).toDouble()

            orderIdTextView.text="Order Id: #${order.orderId}"
            itemCountTextView.text="${order.itemCount} Item"
            orderDateTextView.text="Ordered on ${order.orderedDate}"
            billAmountTextView.text="₹$caad"
            GlobalScope.launch {
                val job=launch (Dispatchers.IO){
                    val orderedList=loader.loadData(list[adapterPosition].orderId)
                    println("Got $orderedList")
                    withContext(Dispatchers.Main){
                        when(order.itemCount){
                            1->{
                                imageView.visibility=View.VISIBLE
                                twoSplitImageView.visibility=View.GONE
                                threeSplitImageView.visibility=View.GONE
                                fourSplitImageView.visibility=View.GONE
                                setImageForImageView(orderedList[0],imageView)
                            }
                            2->{
                                imageView.visibility=View.GONE
                                twoSplitImageView.visibility=View.VISIBLE
                                threeSplitImageView.visibility=View.GONE
                                fourSplitImageView.visibility=View.GONE
                                setImageForImageView(orderedList[0],firstImageInTwoSplit)
                                setImageForImageView(orderedList[1],secondImageInTwoSplit)
                                //setImageForTwoSplitImageView(orderedList)
                            }
                            3->{
                                imageView.visibility=View.GONE
                                twoSplitImageView.visibility=View.GONE
                                threeSplitImageView.visibility=View.VISIBLE
                                fourSplitImageView.visibility=View.GONE
                                setImageForImageView(orderedList[0],firstImageInThreeSplit)
                                setImageForImageView(orderedList[1],secondImageInThreeSplit)
                                setImageForImageView(orderedList[2],thirdImageInThreeSplit)
                            }
                            /*4->{
                                imageView.visibility=View.GONE
                                twoSplitImageView.visibility=View.GONE
                                threeSplitImageView.visibility=View.GONE
                                fourSplitImageView.visibility=View.VISIBLE
                                setImageForImageView(orderedList[0],firstImageInFourSplit)
                                setImageForImageView(orderedList[1],secondImageInFourSplit)
                                setImageForImageView(orderedList[2],thirdImageInFourSplit)
                                setImageForImageView(orderedList[3],fourthImageInFourSplit)
                            }*/
                            else->{
                                imageView.visibility=View.GONE
                                twoSplitImageView.visibility=View.GONE
                                threeSplitImageView.visibility=View.GONE
                                fourSplitImageView.visibility=View.VISIBLE
                                setImageForImageView(orderedList[0],firstImageInFourSplit)
                                setImageForImageView(orderedList[1],secondImageInFourSplit)
                                setImageForImageView(orderedList[2],thirdImageInFourSplit)
                                setImageForImageView(orderedList[3],fourthImageInFourSplit)
                            }
                        }
                    }
                }
                job.join()
            }


            //addressTextView.text=order
        }



        private fun setImageForImageView(orderedProduct: OrderedProduct,imageView: ImageView) {
            var bitmapValue:Bitmap?=null
            val bitmap: Bitmap? = ProductImageMemoryCache.getBitmapFromMemCache(orderedProduct.productId.toString())?.also {
                println("Fetched from cache at $adapterPosition")
                imageView.setImageBitmap(it)
            } ?:run{
                GlobalScope.launch {
                    val job=launch(Dispatchers.IO) {
                        val imageUrl = URL(orderedProduct.thumbnail)
                        withContext(Dispatchers.Main) {
                            imageView.setImageResource(R.drawable.placeholder)
                        }
                        try{
                            bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                        }catch (exception: IOException){
                            println("Exception caught")
                        }
                        //imageView.setImageResource(R.drawable.placeholder)
                        //bitmapValue= BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                        withContext(Dispatchers.Main){
                            if(bitmapValue!=null){
                                imageView.setImageBitmap(bitmapValue)
                                ProductImageMemoryCache.addBitmapToCache(orderedProduct.productId.toString(),bitmapValue!!)
                            }
                        }
                    }
                    job.join()
                }
                null
            }
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