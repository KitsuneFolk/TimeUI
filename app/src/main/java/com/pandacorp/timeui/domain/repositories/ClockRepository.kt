package com.pandacorp.timeui.domain.repositories

import com.pandacorp.timeui.domain.models.ClockItem

interface ClockRepository {
    fun getAll(): MutableList<ClockItem>
    
    fun insertItem(clockItem: ClockItem): Long
    
    fun removeItem(clockItem: ClockItem)
    
    fun removeAll()
}