package com.pandacorp.timeui.domain.models

import java.util.*

data class ClockItem(
    var id: Int = 0,
    var uuid: UUID = UUID.randomUUID(),
    var timeZoneId: String,
    var name: String
)