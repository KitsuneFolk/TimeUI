package com.pandacorp.timeui.data.database

import androidx.room.*
import com.pandacorp.timeui.domain.models.TimerItem

@Dao
interface TimerDao {
    @Query("SELECT * FROM timerItem")
    fun getAll(): MutableList<TimerItem>
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(timerItem: TimerItem)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(timerItems: MutableList<TimerItem>)
    
    @Insert
    fun insert(timerItem: TimerItem)
    
    @Delete
    fun remove(timerItem: TimerItem)
    
    @Query("DELETE FROM timerItem")
    fun removeAll()
}