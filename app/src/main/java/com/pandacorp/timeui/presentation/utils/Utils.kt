package com.pandacorp.timeui.presentation.utils

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pandacorp.timeui.domain.models.ClockItem
import java.util.TimeZone

class Utils {
    companion object {
        val clocksList: List<ClockItem> by lazy {
            TimeZone.getAvailableIDs().map {
                TimeZone.getTimeZone(it)
            }.distinctBy {
                it.displayName
            }.sortedWith(compareBy(
                    { it.displayName.startsWith("GMT") }, // Sort by whether the name starts with "GMT"
                    { it.displayName } // Sort by the display name
            )).map {
                ClockItem(timeZone = it.id)
            }
        }
        
        fun timeToTimeInMillis(hours: Int, minutes: Int, seconds: Int): Long =
            ((hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000)).toLong()
        
        /**
         * This function checks if user removed item when floating action button was hided, and if so, it shows it again, it fixes the bug when fab could been not seen on a screen and recyclerview wasn't scrollable.
         */
        fun handleShowingFAB(recyclerView: RecyclerView, fab: FloatingActionButton) {
            if (!recyclerView.canScrollVertically(1) || !recyclerView.canScrollVertically(-1)) {
                val layoutParams: ViewGroup.LayoutParams = fab.layoutParams
                if (layoutParams is CoordinatorLayout.LayoutParams) {
                    val behavior = layoutParams.behavior
                    if (behavior is HideBottomViewOnScrollBehavior) {
                        if (fab.visibility == View.VISIBLE) {
                            behavior.slideUp(fab)
                        } else {
                            behavior.slideDown(fab)
                        }
                    }
                }
            }
        }
    }
}