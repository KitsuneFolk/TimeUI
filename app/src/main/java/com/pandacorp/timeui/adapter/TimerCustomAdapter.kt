package com.pandacorp.timeui.adapter

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.countdownview.CountdownView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.ui.DBHelper

class TimerCustomAdapter(private var timers: MutableList<TimerListItem>) :
    RecyclerView.Adapter<TimerCustomAdapter.ViewHolder>() {
    private val TAG = "MyLogs"
    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Creating DBHelper object
        db = DBHelper(parent.context, null)

        //Creating WritableDatabase object
        wdb = db.writableDatabase

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timer_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timer = timers[position]

        holder.timer_start_btn.setOnClickListener {
            holder.timer_countdown.start(timer.currentTime)
            timer.isFreeze = 0
            db.updateOneTimerInDatabase(timer, position)
        }
        holder.timer_stop_btn.setOnClickListener {
            holder.timer_countdown.stop()
            timer.isFreeze = 1
            db.updateOneTimerInDatabase(timer, position)


        }
        Log.d(TAG, "onBindViewHolder: timer.isFreeze = ${timer.isFreeze}")

        checkIsFreeze(holder, position)

    }

    private fun checkIsFreeze(holder: ViewHolder, position: Int) {
        // Updating format from database. 0 = false, 1 = true
        Log.d(TAG, "checkIsFreeze: timers[$position].isFreeze = ${timers[position].isFreeze}")
        when (timers[position].isFreeze) {
            1 -> {
                holder.timer_countdown.stop()
                holder.timer_countdown.updateShow(timers[position].currentTime)
            }

            0 -> {
                holder.timer_countdown.start(timers[position].remainTime - System.currentTimeMillis())

                Log.d(
                    TAG,
                    "onBindViewHolder: System.currentTimeInMillis() = ${System.currentTimeMillis()}"
                )
                Log.d(
                    TAG,
                    "onBindViewHolder: timers[position].remainTime = ${timers[position].remainTime}"
                )
                Log.d(
                    TAG,
                    "onBindViewHolder: timers[position].remainTime - System.currentTimeMillis()  = ${timers[position].remainTime - System.currentTimeMillis()}"
                )
            }
//            else -> {throw Exception("value can be only 0 or 1 !")}
        }

    }

    fun removeItem(position: Int) {
        timers.removeAt(position)

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, timers.size)

    }


    override fun getItemCount() = timers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timer_stop_btn = itemView.findViewById<ImageButton>(R.id.timer_stop_btn)
        val timer_start_btn = itemView.findViewById<ImageButton>(R.id.timer_start_btn)
        val timer_countdown = itemView.findViewById<CountdownView>(R.id.timer_countdown)

        val background = itemView.findViewById<RelativeLayout>(R.id.background)
        val foreground = itemView.findViewById<ConstraintLayout>(R.id.foreground)
    }
}