package com.pandacorp.timeui.ui.stopwatch

import android.os.CountDownTimer
import android.widget.TextView
import java.util.*

class Stopwatch(private val textView: TextView) {
    private var seconds = 0L
    private var stopTime = 0L
    
    private var timer: CountDownTimer? = null
    //if use lateinit there is an error.
    
    
    fun start(seconds: Long) {
        this.seconds = seconds
        setTimer()
        timer?.start()
        
        
    }
    
    private fun setTimer() {
        timer = object : CountDownTimer(1000_000_000_000_00, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60
                //600 = 00:06:00
                val time: String = java.lang.String.format(
                        Locale.getDefault(),
                        "%02d:%02d:%02d",
                        hours,
                        minutes,
                        secs
                )
                seconds += 1L
                
                textView.text = time
                
            }
            
            override fun onFinish() {
            
            }
            
        }
        
        
    }
    
    fun setTime(seconds: Long) {
        timer?.cancel()
        this.seconds = seconds
        
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        val secs = seconds % 60
        val time: String = java.lang.String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                secs
        )
        
        textView.text = time
        
    }
    
    fun stop() {
        this.stopTime = System.currentTimeMillis()
        timer?.cancel()
    }
    
    fun getTime(): Long {
        return seconds
    }
    
    fun getElapsedTime(): Long {
        
        return System.currentTimeMillis() - seconds
        
    }
    
    
}