package com.pandacorp.timeui.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimerItem(
    var id: Long = 0,
    var startTime: Long,
    var currentTime: Long,
    var status: Int
) : Parcelable {
    companion object STATUS {
        // Timer was added
        const val ADDED = -2

        // Reset button was clicked
        const val RESET = -1

        // Stop button was clicked
        const val STOPPED = 1

        // Start button was clicked
        const val RUNNING = 0
    }
}
