package com.pandacorp.timeui.data.mappers

import com.pandacorp.timeui.data.models.TimerDataItem
import com.pandacorp.timeui.domain.models.TimerItem
import javax.inject.Inject

class TimerMapper @Inject constructor() {
    fun toTimerItem(timerDataItem: TimerDataItem): TimerItem = TimerItem(
        timerDataItem.id,
        timerDataItem.startTime,
        timerDataItem.currentTime,
        timerDataItem.status
    )

    fun toTimerDataItem(timerItem: TimerItem): TimerDataItem = TimerDataItem(
        timerItem.id,
        timerItem.startTime,
        timerItem.currentTime,
        timerItem.status
    )
}