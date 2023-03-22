package com.pandacorp.timeui.domain.repositories

import com.pandacorp.timeui.domain.models.TimerItem

interface TimerRepository {
    fun getAll(): MutableList<TimerItem>
    
    fun updateItem(timerItem: TimerItem)
    
    fun updateAll(timers: MutableList<TimerItem>)
    
    fun insert(timerItem: TimerItem): Long
    
    fun remove(timerItem: TimerItem)
    
    fun removeAll()
    
}