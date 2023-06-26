package com.pandacorp.timeui.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pandacorp.timeui.data.models.ClockDataItem

@Dao
interface ClockDao {
    @Query("SELECT * FROM clocks_table")
    fun getAll(): MutableList<ClockDataItem>
    
    @Insert
    fun insert(item: ClockDataItem): Long
    
    @Delete
    fun remove(item: ClockDataItem)

    @Query("DELETE FROM clocks_table")
    fun removeAll()
}
    