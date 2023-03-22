package com.pandacorp.timeui.data.database

import androidx.room.*
import com.pandacorp.timeui.data.models.TimerDataItem

@Dao
interface TimerDao {
    @Query("SELECT * FROM timerDataItem")
    fun getAll(): MutableList<TimerDataItem>
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: TimerDataItem)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(items: MutableList<TimerDataItem>)
    
    @Insert
    fun insert(item: TimerDataItem): Long
    
    @Delete
    fun remove(item: TimerDataItem)
    
    @Query("DELETE FROM timerDataItem")
    fun removeAll()
}