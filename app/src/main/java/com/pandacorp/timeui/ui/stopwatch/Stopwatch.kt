package com.pandacorp.timeui.ui.stopwatch

import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import java.util.*

class Stopwatch(private val textView: TextView) {
    private var milliseconds = 0L
    private var stopTime = 0L
    
    private var timer: CountDownTimer? = null
    //if use lateinit there is an error.
    
    fun start(milliseconds: Long) {
        Log.d("MyLogs", "start: milliseconds = $milliseconds")
        this.milliseconds = milliseconds
        setTimer()
        timer?.start()
        
        
    }
    
    private fun setTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(1000_000_000_000_00, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = milliseconds / 1000
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
                milliseconds += 1000L
                
                textView.text = time
                
            }
            
            override fun onFinish() {
            
            }
            
        }
        
        
    }
    
    fun setTime(milliseconds: Long) {
        Log.d("MyLogs", "setTime: milliseconds = $milliseconds")
        timer?.cancel()
        this.milliseconds = milliseconds
        
        val seconds = milliseconds / 1000
        
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        val secs = seconds % 60
        
        //00:00:50 = 50 000 / 1000 = 50 % 60 = 50
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
        return milliseconds
    }
    
    fun getElapsedTime(): Long {
    
        return System.currentTimeMillis() - milliseconds
        
    }
    
    
}