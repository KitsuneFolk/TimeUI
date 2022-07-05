package com.pandacorp.timeui.adapter

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            if (timer.isFreeze == true) {
                //
                holder.timer_countdown.start(timer.currentTime)

                holder.timer_stop_btn.visibility = View.VISIBLE
                holder.timer_reset_btn.visibility = View.INVISIBLE
                db.updateOneTimerInDatabase(timer, position)
            } else {
                holder.timer_countdown.start(timer.startTime)

                timer.isFreeze == true

                holder.timer_stop_btn.visibility = View.VISIBLE
                holder.timer_reset_btn.visibility = View.INVISIBLE
                db.updateOneTimerInDatabase(timer, position)
            }


        }
        holder.timer_reset_btn.setOnClickListener {
            holder.timer_countdown.stop()

            timer.isFreeze = false
            holder.timer_stop_btn.visibility = View.VISIBLE
            holder.timer_reset_btn.visibility = View.INVISIBLE
            db.updateOneTimerInDatabase(timer, position)

            resetItem(timer, position)
            holder.timer_countdown.updateShow(timer.startTime)
        }
        holder.timer_stop_btn.setOnClickListener {
            holder.timer_countdown.stop()

            timer.currentTime = holder.timer_countdown.remainTime

            timer.isFreeze = true
            holder.timer_stop_btn.visibility = View.INVISIBLE
            holder.timer_reset_btn.visibility = View.VISIBLE
            db.updateOneTimerInDatabase(timer, position)


        }
        Log.d(TAG, "onBindViewHolder: timer.isFreeze = ${timer.isFreeze}")

        checkIsFreeze(holder, position)

    }

    private fun checkIsFreeze(holder: ViewHolder, position: Int) {
        // Updating format from database. 0 = false, 1 = true
        Log.d(TAG, "checkIsFreeze: timers[$position].isFreeze = ${timers[position].isFreeze}")
        when (timers[position].isFreeze) {
            true -> {
                holder.timer_countdown.stop()
                holder.timer_countdown.updateShow(timers[position].currentTime)
                holder.timer_stop_btn.visibility = View.INVISIBLE
                holder.timer_reset_btn.visibility = View.VISIBLE

            }

            false -> {
                holder.timer_countdown.start(timers[position].remainTime - System.currentTimeMillis())
                holder.timer_stop_btn.visibility = View.VISIBLE
                holder.timer_reset_btn.visibility = View.INVISIBLE

            }
//            else -> {throw Exception("value can be only 0 or 1 !")}
        }

    }


    private fun resetItem(timer: TimerListItem, position: Int) {
        val cv = ContentValues()
        cv.put(DBHelper.START_TIME_COL, timer.startTime)
        cv.put(DBHelper.CURRENT_TIME_COL, timer.startTime)
        cv.put(DBHelper.REMAIN_TIME_COl, timer.startTime)
        cv.put(
            DBHelper.IS_FREEZE_COl, when (timer.isFreeze) {
                false -> 0
                true -> 1

            }
        )
        val id = db.getDatabaseItemIdByRecyclerViewItemId(position)
        wdb.update(DBHelper.TIMER_TABLE, cv, "id = ?", arrayOf(id.toString()))

        timer.currentTime = timer.startTime
        timer.remainTime = timer.startTime
        timer.isFreeze = true

    }

    fun removeItem(position: Int) {
        timers.removeAt(position)

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, timers.size)

    }

    override fun getItemCount() = timers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timer_stop_btn = itemView.findViewById<ImageButton>(R.id.timer_stop_btn)
        val timer_reset_btn = itemView.findViewById<ImageButton>(R.id.timer_reset_btn)
        val timer_start_btn = itemView.findViewById<ImageButton>(R.id.timer_start_btn)
        val timer_countdown = itemView.findViewById<CountdownView>(R.id.timer_countdown)

        //        val background = itemView.findViewById<RelativeLayout>(R.id.background)
        val foreground = itemView.findViewById<ConstraintLayout>(R.id.foreground)
    }
}