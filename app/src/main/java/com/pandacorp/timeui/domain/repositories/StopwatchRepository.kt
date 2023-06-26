package com.pandacorp.timeui.domain.repositories

import com.pandacorp.timeui.domain.models.StopwatchItem

interface StopwatchRepository {
    fun getAll(): MutableList<StopwatchItem>
    
    fun updateItem(stopwatchItem: StopwatchItem)
    
    fun updateAll(stopwatches: MutableList<StopwatchItem>)
    
    fun insertItem(stopwatchItem: StopwatchItem): Long
    
    fun removeItem(stopwatchItem: StopwatchItem)
    
    fun removeAll()
}