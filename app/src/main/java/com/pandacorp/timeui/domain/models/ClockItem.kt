package com.pandacorp.timeui.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClockItem(var id: Long = 0, var timeZone: String) : Parcelable