package com.pandacorp.timeui.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pandacorp.timeui.domain.models.StopwatchItem

@Entity(tableName = "stopwatches_table")
data class StopwatchDataItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "startSysTime") val startSysTime: Long = 0L,
    @ColumnInfo(name = "stopTime") val stopTime: Long = 0,
    @ColumnInfo(name = "status") val status: Int = StopwatchItem.ADDED
)