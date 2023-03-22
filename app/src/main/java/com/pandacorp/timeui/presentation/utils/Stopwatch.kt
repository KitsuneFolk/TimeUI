package com.pandacorp.timeui.presentation.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class Stopwatch {
    
    companion object {
        private const val MSG = 1
    }
    
    private val mCountdownInterval: Long = 1000L
    
    /**
     * boolean representing if the timer was cancelled
     */
    private var mCancelled = false
    
    /**
     * Cancel the countdown.
     */
    @Synchronized
    fun cancel() {
        mCancelled = true
        mHandler.removeMessages(MSG)
    }
    
    /**
     * Start the countdown.
     */
    @Synchronized
    fun start() {
        mCancelled = false
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
    }
    
    abstract fun onTick()
    
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            synchronized(this@Stopwatch) {
                if (mCancelled) {
                    return
                }
                onTick()
                sendMessageDelayed(obtainMessage(MSG), mCountdownInterval)
            }
        }
    }
    
}