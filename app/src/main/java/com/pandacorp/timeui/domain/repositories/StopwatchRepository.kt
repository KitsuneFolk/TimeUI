package com.pandacorp.timeui.domain.repositories

import com.pandacorp.timeui.domain.models.StopwatchItem

interface StopwatchRepository {
    fun getAll(): MutableList<StopwatchItem>
    
    fun updateItem(item: StopwatchItem)
    
    fun updateAll(items: MutableList<StopwatchItem>)
    
    fun insert(item: StopwatchItem)
    
    fun remove(item: StopwatchItem)
    
    fun removeAll()
    
}