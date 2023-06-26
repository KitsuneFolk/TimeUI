package com.pandacorp.timeui.data.repositories

import com.pandacorp.timeui.data.database.TimerDao
import com.pandacorp.timeui.data.mappers.TimerMapper
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.domain.repositories.TimerRepository

class TimerRepositoryImpl(private val timerDao: TimerDao, private val mapper: TimerMapper) :
    TimerRepository {
    override fun getAll(): MutableList<TimerItem> =
        timerDao.getAll().map { mapper.toTimerItem(it) }.toMutableList()
    
    override fun updateItem(timerItem: TimerItem) = timerDao.update(mapper.toTimerDataItem(timerItem))

    override fun updateAll(timers: MutableList<TimerItem>) =
        timerDao.updateAll(timers.map { mapper.toTimerDataItem(it) }.toMutableList())

    override fun insert(timerItem: TimerItem): Long = timerDao.insert(mapper.toTimerDataItem(timerItem))

    override fun remove(timerItem: TimerItem) = timerDao.remove(mapper.toTimerDataItem(timerItem))

    override fun removeAll() = timerDao.removeAll()
}
