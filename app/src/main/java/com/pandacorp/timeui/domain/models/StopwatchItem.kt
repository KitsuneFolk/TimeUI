package com.pandacorp.timeui.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StopwatchItem(
    var id: Long = 0,
    var startSysTime: Long = START_TIME,
    var stopTime: Long = START_TIME,
    var status: Int = ADDED
) : Parcelable {

    companion object {
        // Stopwatch was added
        const val ADDED = -2

        // Reset button was clicked
        const val RESET = -1

        // Stop button was clicked
        const val STOPPED = 1

        // Start button was clicked
        const val RUNNING = 0

        const val START_TIME = 0L
    }
}