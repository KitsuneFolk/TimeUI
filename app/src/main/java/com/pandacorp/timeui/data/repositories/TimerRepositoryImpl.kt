package com.pandacorp.timeui.data.repositories

import com.pandacorp.timeui.data.database.TimerDao
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.domain.repositories.TimerRepository

class TimerRepositoryImpl(private val timerDao: TimerDao) : TimerRepository {
    override fun getAll(): MutableList<TimerItem> = timerDao.getAll()
    
    override fun updateItem(timerItem: TimerItem) {
        timerDao.update(timerItem)
    }
    
    override fun updateAll(timers: MutableList<TimerItem>) {
        timerDao.updateAll(timers)
    }
    
    override fun insert(timerItem: TimerItem) {
        timerDao.insert(timerItem)
    }
    
    override fun remove(timerItem: TimerItem) {
        timerDao.remove(timerItem)
    }
    
    override fun removeAll() {
        timerDao.removeAll()
    }
    
    
}
