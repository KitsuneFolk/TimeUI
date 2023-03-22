package com.pandacorp.timeui.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StopwatchDataItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "startSysTime") val startSysTime: Long = 0L,
    @ColumnInfo(name = "stopTime") val stopTime: Long = 0,
    @ColumnInfo(name = "status") val status: Int = ADDED
) {
    
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
        
    }
}