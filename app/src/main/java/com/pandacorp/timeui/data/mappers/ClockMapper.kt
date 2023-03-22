package com.pandacorp.timeui.data.mappers

import com.pandacorp.timeui.data.models.ClockDataItem
import com.pandacorp.timeui.domain.models.ClockItem
import javax.inject.Inject

class ClockMapper @Inject constructor() {
    fun toClockItem(clockDataItem: ClockDataItem): ClockItem {
        return ClockItem(
                clockDataItem.id,
                clockDataItem.timeZone,
                clockDataItem.name)
    }
    
    fun toClockDataItem(clockItem: ClockItem): ClockDataItem {
        return ClockDataItem(
                clockItem.id,
                clockItem.timeZoneId,
                clockItem.name
        )
    }
}