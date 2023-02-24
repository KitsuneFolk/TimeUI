package com.pandacorp.timeui.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class TimerItem(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "startTime") var startTime: Long,
    @ColumnInfo(name = "currentTime") var currentTime: Long,
    @ColumnInfo(name = "status") var status: Int,
    @ColumnInfo(name = "uuid") var uuid: UUID = UUID.randomUUID()
) : Serializable {
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