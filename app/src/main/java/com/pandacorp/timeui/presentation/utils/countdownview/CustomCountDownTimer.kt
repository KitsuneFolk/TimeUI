package com.pandacorp.timeui.presentation.utils.countdownview

import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class CustomCountDownTimer(countDownInterval: Long) {
    private val mCountdownInterval: Long = countDownInterval
    private var isRunning = false

    var milliseconds: Long = 0
        private set

    @Synchronized
    fun start(milliseconds: Long = this.milliseconds) {
        isRunning = true
        this.milliseconds = milliseconds
        if (milliseconds <= 0) {
            onFinish()
            isRunning = false
            return
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
    }

    @Synchronized
    fun cancel() {
        isRunning = false
        mHandler.removeMessages(MSG)
    }

    abstract fun onTick(millisUntilFinished: Long)
    abstract fun onFinish()

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            synchronized(this@CustomCountDownTimer) {
                if (milliseconds <= 0) {
                    onFinish()
                    isRunning = false
                }
                else {
                    val lastTickStart = System.currentTimeMillis()
                    onTick(milliseconds)
                    milliseconds -= mCountdownInterval
                    var delay = lastTickStart + mCountdownInterval - System.currentTimeMillis()
                    while (delay < 0) delay += mCountdownInterval
                    sendMessageDelayed(obtainMessage(MSG), delay)
                }
            }
        }
    }

    companion object {
        private const val MSG = 1
    }
}