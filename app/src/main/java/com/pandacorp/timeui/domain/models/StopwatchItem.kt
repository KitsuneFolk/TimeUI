package com.pandacorp.timeui.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class StopwatchItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "uuid") var uuid: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "startSysTime") var startSysTime: Long = 0L,
    @ColumnInfo(name = "stopTime") var stopTime: Long = 0,
    @ColumnInfo(name = "status") var status: Int = ADDED
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