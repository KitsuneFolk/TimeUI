package com.pandacorp.timeui.data.mappers

import com.pandacorp.timeui.data.models.StopwatchDataItem
import com.pandacorp.timeui.domain.models.StopwatchItem
import javax.inject.Inject

class StopwatchMapper @Inject constructor() {
    fun toStopwatchItem(stopwatchDataItem: StopwatchDataItem): StopwatchItem {
        return StopwatchItem(
                stopwatchDataItem.id,
                stopwatchDataItem.startSysTime,
                stopwatchDataItem.stopTime,
                stopwatchDataItem.status)
    }
    
    fun toStopwatchDataItem(stopwatchItem: StopwatchItem): StopwatchDataItem {
        return StopwatchDataItem(
                stopwatchItem.id,
                stopwatchItem.startSysTime,
                stopwatchItem.stopTime,
                stopwatchItem.status)
    }
}