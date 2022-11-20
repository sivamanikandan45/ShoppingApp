package com.example.shopping.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R


abstract class SwipeToDeleteCallback internal constructor(context: Context) :
    ItemTouchHelper.Callback() {
    var context: Context
    private val clearPaint: Paint
    private val background: ColorDrawable
    private val backgroundColor: Int
    private val deleteDrawable: Drawable?
    private val intrinsicWidth: Int
    private val intrinsicHeight: Int

    override fun getMovementFlags(recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView= viewHolder.itemView
        val itemHeight: Int = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c, itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                c,
                recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
            )
            return
        }
        background.color = backgroundColor
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)
        val deleteIconTop: Int = itemView.getTop() + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft: Int = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight: Int = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        deleteDrawable!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    /*override fun getSwipeThreshold(@NonNull viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }*/

    init {
        this.context = context
        background = ColorDrawable()
        backgroundColor = Color.parseColor("#b80f0a")
        clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        deleteDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_baseline_delete_24)
        intrinsicWidth = deleteDrawable!!.intrinsicWidth
        intrinsicHeight = deleteDrawable.intrinsicHeight
    }
}