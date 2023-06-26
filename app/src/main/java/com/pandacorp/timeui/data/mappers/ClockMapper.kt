package com.pandacorp.timeui.data.mappers

import com.pandacorp.timeui.data.models.ClockDataItem
import com.pandacorp.timeui.domain.models.ClockItem
import javax.inject.Inject

class ClockMapper @Inject constructor() {
    fun toClockItem(clockDataItem: ClockDataItem): ClockItem = ClockItem(
        clockDataItem.id,
        clockDataItem.timeZone
    )

    fun toClockDataItem(clockItem: ClockItem): ClockDataItem = ClockDataItem(
        clockItem.id,
        clockItem.timeZone
    )
}