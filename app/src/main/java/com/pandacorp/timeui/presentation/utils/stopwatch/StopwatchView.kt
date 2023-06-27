package com.pandacorp.timeui.presentation.utils.stopwatch

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StopwatchView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var stopwatch: Stopwatch? = null
    private var milliseconds: Long = -1000L

    private var mRegistered = false
    private var isRunning = false

    private var mPreviousIntervalCallbackTime: Long? = null

    /**
     * Start the stopwatch with given milliseconds
     * @param milliseconds the millisecond we should start count from
     */
    fun start(milliseconds: Long = this.milliseconds) {
        stopwatch?.cancel()
        stopwatch = null

        mPreviousIntervalCallbackTime = null
        isRunning = true
        this.milliseconds = milliseconds - 1000

        setTimer()
    }

    /**
     * Set the stopwatch time text with given milliseconds
     * @param milliseconds Long variable
     */
    fun setTime(milliseconds: Long = 0) {
        this.milliseconds = milliseconds
        text = getFormattedTime(milliseconds)
    }

    /**
     * Cancel the stopwatch
     */
    fun cancel() {
        stopwatch?.cancel()
        stopwatch = null
        isRunning = false
        mPreviousIntervalCallbackTime = null
    }

    fun getTime(): Long = milliseconds

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mRegistered || isRunning) {
            stopwatch?.cancel()
            mRegistered = false
            mPreviousIntervalCallbackTime = System.currentTimeMillis()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!mRegistered && isRunning) {
            mRegistered = true
            mPreviousIntervalCallbackTime?.let {
                setTime(milliseconds + System.currentTimeMillis() - it)
            }
            start()
        }
    }

    private fun setTimer() {
        stopwatch = object : Stopwatch() {
            override fun onTick() {
                milliseconds += 1000L
                val time = getFormattedTime(milliseconds)
                text = time
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
}