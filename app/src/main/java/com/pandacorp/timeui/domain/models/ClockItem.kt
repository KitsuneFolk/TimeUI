package com.pandacorp.timeui.domain.models

data class ClockItem(
    var id: Long = 0,
    var timeZoneId: String,
    var name: String
)