package com.pandacorp.timeui.presentation.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.TypedValue
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.presentation.ui.adapters.StopwatchAdapter
import com.pandacorp.timeui.presentation.ui.adapters.TimerAdapter
import com.pandacorp.timeui.presentation.ui.adapters.clocks.ClockAdapter

class CustomItemTouchHelper(
    context: Context,
    private val key: Constants.ITHKey,
    private val itemTouchHelperListener: ItemTouchHelperListener
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    interface ItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int)
    }

    var isRoundCornersEnabled: Boolean = true

    private var isRoundCorners = false

    private val backgroundColor: Int by lazy {
        val tv = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)
        tv.data
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder is ClockAdapter.ViewHolder && viewHolder.bindingAdapterPosition == 0) return 0 // don't allow to remove default clocks
        return super.getSwipeDirs(recyclerView, viewHolder)
    }
    
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false
    
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemTouchHelperListener.onSwiped(viewHolder, direction)
    }
    
    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView = getForegroundView(viewHolder)
        
        setCorners(dX, isCurrentlyActive, foregroundView)
        
        getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
        )
    }
    
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = viewHolder.itemView
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }
    
    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView = getForegroundView(viewHolder)
        
        getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
        )
    }
    
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = getForegroundView(viewHolder)
        
        setCorners(0f, true, foregroundView)
        
        getDefaultUIUtil().clearView(foregroundView)
    }
    
    /**
     * Set rounded corners if dX < 0 (for swipe from right to left) and remove if dx == 0
     */
    private fun setCorners(
        dX: Float,
        isCurrentlyActive: Boolean,
        foregroundView: ViewGroup
    ) {
        if (!isRoundCornersEnabled) return
    
        if (dX < 0) { // add rounded corners
            if (isRoundCorners) {
                isRoundCorners = false
                val shapeDrawable = ShapeDrawable(
                        RoundRectShape(
                                floatArrayOf(
                                        0f,
                                        0f,
                                        CORNERS_RADIUS,
                                        CORNERS_RADIUS,
                                        CORNERS_RADIUS,
                                        CORNERS_RADIUS,
                                        0f,
                                        0f),
                                null,
                                null))
                shapeDrawable.paint.color = backgroundColor
                foregroundView.background = shapeDrawable
                
                ValueAnimator.ofFloat(0f, CORNERS_RADIUS).apply {
                    duration = 250
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        shapeDrawable.shape = RoundRectShape(
                                floatArrayOf(0f, 0f, value, value, value, value, 0f, 0f),
                                null,
                                null)
                        shapeDrawable.paint.color = backgroundColor
                        foregroundView.background = shapeDrawable
                    }
                }.start()
            }
        } else { // remove rounded corners
            if (!isCurrentlyActive) return // needed to remove bug when rounded corners added and removed twice or thrice
            if (!isRoundCorners) { // check if else branch called first time
                isRoundCorners = true
                val shapeDrawable = ShapeDrawable(
                        RoundRectShape(
                                floatArrayOf(
                                        0f,
                                        0f,
                                        CORNERS_RADIUS,
                                        CORNERS_RADIUS,
                                        CORNERS_RADIUS,
                                        CORNERS_RADIUS,
                                        0f,
                                        0f),
                                null,
                                null))
                shapeDrawable.paint.color = backgroundColor
                foregroundView.background = shapeDrawable
                
                ValueAnimator.ofFloat(CORNERS_RADIUS, 0f).apply {
                    duration = 250
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        shapeDrawable.shape = RoundRectShape(
                            floatArrayOf(0f, 0f, value, value, value, value, 0f, 0f),
                            null,
                            null
                        )
                        foregroundView.background = shapeDrawable
                    }
                }.start()
            }
        }
    }

    private fun getForegroundView(viewHolder: RecyclerView.ViewHolder): ViewGroup = when (key) {
        Constants.ITHKey.TIMER -> (viewHolder as TimerAdapter.ViewHolder).binding.foreground
        Constants.ITHKey.STOPWATCH -> (viewHolder as StopwatchAdapter.ViewHolder).binding.foreground
        Constants.ITHKey.CLOCK -> (viewHolder as ClockAdapter.ViewHolder).binding.foreground
    }

    companion object {
        private const val CORNERS_RADIUS = 15f
    }
}