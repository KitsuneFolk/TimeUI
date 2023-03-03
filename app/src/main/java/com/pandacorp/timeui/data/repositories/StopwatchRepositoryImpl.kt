package com.pandacorp.timeui.data.repositories

import com.pandacorp.timeui.data.database.StopwatchDao
import com.pandacorp.timeui.data.mappers.StopwatchMapper
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.domain.repositories.StopwatchRepository

class StopwatchRepositoryImpl(private val dao: StopwatchDao, private val mapper: StopwatchMapper) :
    StopwatchRepository {
    override fun getAll(): MutableList<StopwatchItem> =
        dao.getAll().map { mapper.toStopwatchItem(it) }.toMutableList()
    
    override fun updateItem(stopwatchItem: StopwatchItem) {
        dao.update(mapper.toStopwatchDataItem(stopwatchItem))
    }
    
    override fun updateAll(stopwatches: MutableList<StopwatchItem>) {
        dao.updateAll(stopwatches.map { mapper.toStopwatchDataItem(it) }.toMutableList())
    }
    
    override fun insertItem(stopwatchItem: StopwatchItem) {
        dao.insert(mapper.toStopwatchDataItem(stopwatchItem))
    }
    
    override fun removeItem(stopwatchItem: StopwatchItem) {
        dao.remove(mapper.toStopwatchDataItem(stopwatchItem))
    }
    
    override fun removeAll() {
        dao.removeAll()
    }
    
    
}
