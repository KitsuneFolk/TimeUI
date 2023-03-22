package com.pandacorp.timeui.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimerDataItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "startTime") val startTime: Long,
    @ColumnInfo(name = "currentTime") val currentTime: Long,
    @ColumnInfo(name = "status") val status: Int
) {
    companion object STATUS {
        //If timer was added
        const val ADDED = -2
        
        //If reset btn was clicked
        const val RESETED = -1
        
        //If stop btn was clicked
        const val STOPED = 1
        
        //If start btn was clicked
        const val RUNNING = 0
    }
}