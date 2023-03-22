package com.pandacorp.timeui.domain.models

import java.io.Serializable

data class StopwatchItem(
    var id: Long = 0,
    var startSysTime: Long = 0L,
    var stopTime: Long = 0,
    var status: Int = ADDED
) : Serializable {
    
    companion object {
        // If timer was added
        const val ADDED = 0
        
        // If reset btn was clicked
        const val RESETED = 1
        
        // If stop btn was clicked
        const val STOPED = 2
        
        // If start btn was clicked
        const val RUNNING = 3
        
        // Default start time
        const val START_TIME = 0L
        
        fun create(): StopwatchItem {
            return StopwatchItem()
        }
    }
}