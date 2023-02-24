package com.pandacorp.timeui.presentation.utils

class Utils {
    companion object {
        const val TAG = "CustomUtils"
        
        //This function is needed for coroutines logs work on Xiaomi devices.
        fun setupExceptionHandler() {
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                throw(throwable)
                
            }
        }
        
        fun timeToTimeInMillis(hours: Int, minutes: Int, seconds: Int): Long =
            ((hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000)).toLong()
    }
}