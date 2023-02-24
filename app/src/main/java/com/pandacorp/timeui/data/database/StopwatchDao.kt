package com.pandacorp.timeui.data.database

import androidx.room.*
import com.pandacorp.timeui.domain.models.StopwatchItem

@Dao
interface StopwatchDao {
    @Query("SELECT * FROM stopwatchItem")
    fun getAll(): MutableList<StopwatchItem>
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: StopwatchItem)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(items: MutableList<StopwatchItem>)
    
    @Insert
    fun insert(item: StopwatchItem)
    
    @Delete
    fun remove(item: StopwatchItem)
    
    @Query("DELETE FROM stopwatchItem")
    fun removeAll()
    
}
