package com.pandacorp.timeui.adapter

data class TimerListItem(var currentTime: Long, var remainTime: Long, var isFreeze: Int = 0)