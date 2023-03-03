package com.pandacorp.timeui.domain.models

import java.io.Serializable
import java.util.*

data class TimerItem(
    var id: Int = 0,
    var uuid: UUID = UUID.randomUUID(),
    var startTime: Long,
    var currentTime: Long,
    var status: Int
) : Serializable {
    companion object STATUS {
        //If timer was added
        const val ADDED = -2
        
        //If reset btn was clicked
        const val RESETED = -1
        
        //If stop btn was clicked
        const val STOPED = 1
        
        //If start btn was clicked
        const val RUNNING = 0
    }
}
