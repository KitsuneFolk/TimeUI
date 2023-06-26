package com.pandacorp.timeui.data.database

import androidx.room.*
import com.pandacorp.timeui.data.models.StopwatchDataItem

@Dao
interface StopwatchDao {
    @Query("SELECT * FROM stopwatches_table ORDER BY id DESC")
    fun getAll(): MutableList<StopwatchDataItem>
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: StopwatchDataItem)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(items: MutableList<StopwatchDataItem>)
    
    @Insert
    fun insert(item: StopwatchDataItem): Long
    
    @Delete
    fun remove(item: StopwatchDataItem)

    @Query("DELETE FROM stopwatches_table")
    fun removeAll()
    
}
