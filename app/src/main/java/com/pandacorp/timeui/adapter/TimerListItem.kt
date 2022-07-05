package com.pandacorp.timeui.adapter

data class TimerListItem(
    var startTime: Long,
    var currentTime: Long,
    var remainTime: Long,
    var isFreeze: Boolean
)