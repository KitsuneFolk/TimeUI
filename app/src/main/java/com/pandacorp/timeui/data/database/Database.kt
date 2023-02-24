package com.pandacorp.timeui.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.domain.models.TimerItem

@Database(entities = [TimerItem::class, StopwatchItem::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun timerDao(): TimerDao
    abstract fun stopwatchDao(): StopwatchDao
    
}