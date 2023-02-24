package com.pandacorp.timeui.presentation.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import com.pandacorp.timeui.presentation.ui.stopwatch.adapter.StopwatchAdapter

class StopwatchView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)
    
    companion object {
        private const val TAG = StopwatchAdapter.TAG
    }
    
    private var stopwatch: Stopwatch? = null
    private var milliseconds: Long = -1000L
    
    /**
     * Start the stopwatch with given milliseconds;
     * @param milliseconds - the millisecond we should start count from
     */
    fun start(milliseconds: Long = 0L) {
        Log.d(TAG, "start: milliseconds = $milliseconds")
        stopwatch?.cancel()
        stopwatch = null
        this.milliseconds = milliseconds - 1000L // Add delay
        setTimer()
    }
    
    private fun setTimer() {
        stopwatch = object : Stopwatch() {
            override fun onTick() {
                milliseconds += 1000L
                val time = getFormattedTime(milliseconds)
                text = time
                Log.d(TAG, "onTick: text = $text")
            }
            
        }
        stopwatch?.start()
    }
    
    private fun getFormattedTime(time: Long): String {
        val seconds = (time / 1000) % 60
        val minutes = (time / 1000 / 60) % 60
        val hours = (time / 1000 / 3600) % 24
        val days = (time / 1000 / 3600 / 24) % 365
        val pattern = when (days) {
            0L -> "%02d:%02d:%02d"
            else -> "%02d:%02d:%02d:%02d"
        }
        return when (days) {
            0L -> String.format(pattern, hours, minutes, seconds)
            else -> String.format(pattern, days, hours, minutes, seconds)
        }
    }
    
    fun setTime(milliseconds: Long) {
        Log.d(TAG, "setTime: time = $milliseconds")
        stopwatch?.cancel()
        
        this.milliseconds = milliseconds
        text = getFormattedTime(milliseconds)
    }
    
    fun cancel() {
        stopwatch?.cancel()
    }
    
    fun getTime(): Long {
        return milliseconds
    }
}