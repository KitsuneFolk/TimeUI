package com.pandacorp.timeui.presentation.utils.stopwatch

import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class Stopwatch {
    companion object {
        private const val MSG = 1
    }
    
    private val mCountdownInterval: Long = 1000L
    
    private var mCancelled = false
    
    @Synchronized
    fun cancel() {
        mCancelled = true
        mHandler.removeMessages(MSG)
    }
    
    @Synchronized
    fun start() {
        mCancelled = false
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
    }
    
    abstract fun onTick()
    
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            synchronized(this@Stopwatch) {
                if (mCancelled) return
                onTick()
                sendMessageDelayed(obtainMessage(MSG), mCountdownInterval)
            }
        }
    }
}