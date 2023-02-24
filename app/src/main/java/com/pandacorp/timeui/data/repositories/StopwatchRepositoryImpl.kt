package com.pandacorp.timeui.data.repositories

import com.pandacorp.timeui.data.database.StopwatchDao
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.domain.repositories.StopwatchRepository

class StopwatchRepositoryImpl(private val dao: StopwatchDao) : StopwatchRepository {
    override fun getAll(): MutableList<StopwatchItem> = dao.getAll()
    
    override fun updateItem(item: StopwatchItem) {
        dao.update(item)
    }
    
    override fun updateAll(items: MutableList<StopwatchItem>) {
        dao.updateAll(items)
    }
    
    override fun insert(item: StopwatchItem) {
        dao.insert(item)
    }
    
    override fun remove(item: StopwatchItem) {
        dao.remove(item)
    }
    
    override fun removeAll() {
        dao.removeAll()
    }
    
    
}
