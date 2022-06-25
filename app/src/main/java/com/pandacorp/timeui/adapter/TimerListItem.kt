package com.pandacorp.timeui.adapter

data class TimerListItem (var currentTime: Long, var remainTime: String, var freezeTime: Boolean = false)