package com.pandacorp.timeui.data.repositories

import com.pandacorp.timeui.data.database.ClockDao
import com.pandacorp.timeui.data.mappers.ClockMapper
import com.pandacorp.timeui.domain.models.ClockItem
import com.pandacorp.timeui.domain.repositories.ClockRepository

class ClockRepositoryImpl(private val dao: ClockDao, private val mapper: ClockMapper) :
    ClockRepository {
    override fun getAll(): MutableList<ClockItem> =
        dao.getAll().map { mapper.toClockItem(it) }.toMutableList()
    
    override fun insertItem(clockItem: ClockItem): Long = dao.insert(mapper.toClockDataItem(clockItem))

    override fun removeItem(clockItem: ClockItem) = dao.remove(mapper.toClockDataItem(clockItem))

    override fun removeAll() = dao.removeAll()
}