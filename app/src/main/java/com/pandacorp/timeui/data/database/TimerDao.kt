package com.pandacorp.timeui.data.database

import androidx.room.*
import com.pandacorp.timeui.data.models.TimerDataItem

@Dao
interface TimerDao {
    @Query("SELECT * FROM timerDataItem")
    fun getAll(): MutableList<TimerDataItem>
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(TimerDataItem: TimerDataItem)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(TimerDataItems: MutableList<TimerDataItem>)
    
    @Insert
    fun insert(TimerDataItem: TimerDataItem)
    
    @Delete
    fun remove(TimerDataItem: TimerDataItem)
    
    @Query("DELETE FROM TimerDataItem")
    fun removeAll()
}