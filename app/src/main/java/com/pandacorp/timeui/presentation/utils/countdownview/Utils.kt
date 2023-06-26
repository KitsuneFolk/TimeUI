package com.pandacorp.timeui.presentation.utils.countdownview

import android.content.Context

internal object Utils {
    fun dp2px(context: Context?, dpValue: Float): Int {
        if (dpValue <= 0) return 0
        val scale = context!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun sp2px(context: Context?, spValue: Float): Float {
        if (spValue <= 0) return 0f
        val scale = context!!.resources.displayMetrics.scaledDensity
        return spValue * scale
    }

    fun formatNum(time: Int): String {
        return if (time < 10) "0$time" else time.toString()
    }

    fun formatMillisecond(millisecond: Int): String {
        val retMillisecondStr: String = if (millisecond > 99) {
            (millisecond / 10).toString()
        } else if (millisecond <= 9) {
            "0$millisecond"
        } else {
            millisecond.toString()
        }
        return retMillisecondStr
    }
}