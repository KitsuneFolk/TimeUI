package com.pandacorp.timeui.data.database

import androidx.room.*
import com.pandacorp.timeui.data.models.ClockDataItem

@Dao
interface ClockDao {
    @Query("SELECT * FROM clockDataItem")
    fun getAll(): MutableList<ClockDataItem>
    
    @Insert
    fun insert(item: ClockDataItem)
    
    @Delete
    fun remove(item: ClockDataItem)
    
    @Query("DELETE FROM clockDataItem")
    fun removeAll()
}
    