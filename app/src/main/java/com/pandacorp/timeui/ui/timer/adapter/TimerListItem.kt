package com.pandacorp.timeui.ui.timer.adapter

data class TimerListItem(
    var startTime: Long,
    var currentTime: Long,
    var remainTime: Long,
    var status: Int
) {
    companion object STATUS {
        //If timer was added
        val ADDED = -2

        //If reset btn was clicked
        val RESETED = -1

        //If stop btn was clicked
        val FREEZED = 1

        //If timer is running and nothing was clicked
        val RUNNING = 0
    }
}