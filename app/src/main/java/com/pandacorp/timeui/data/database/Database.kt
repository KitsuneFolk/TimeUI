package com.pandacorp.timeui.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pandacorp.timeui.data.models.StopwatchDataItem
import com.pandacorp.timeui.data.models.TimerDataItem

@Database(
        entities = [TimerDataItem::class, StopwatchDataItem::class],
        version = 1,
        exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun timerDao(): TimerDao
    abstract fun stopwatchDao(): StopwatchDao
    
}