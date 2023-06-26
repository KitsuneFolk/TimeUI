package com.pandacorp.timeui.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pandacorp.timeui.domain.models.TimerItem

@Entity(tableName = "timers_table")
data class TimerDataItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "startTime") val startTime: Long = 0,
    @ColumnInfo(name = "currentTime") val currentTime: Long = 0,
    @ColumnInfo(name = "status") val status: Int = TimerItem.ADDED
)