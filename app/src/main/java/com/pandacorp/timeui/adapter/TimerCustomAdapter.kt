package com.pandacorp.timeui.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.countdownview.CountdownView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.ui.DBHelper
import com.pandacorp.timeui.ui.timer.TimerActivity
import kotlinx.android.synthetic.main.timer_list_item.view.*

class TimerCustomAdapter(
    private var context: Context,
    private var timers: MutableList<TimerListItem>
) :
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
        holder.timer_countdown.setOnClickListener {
            val intent = Intent(context, TimerActivity::class.java)
            intent.putExtra("list_id", position)
            intent.putExtra("startTime", timer.startTime)
            intent.putExtra("currentTime", timer.currentTime)
            intent.putExtra("remainTime", timer.remainTime)
            intent.putExtra("status", timer.status)

            context.startActivity(intent)
        }
        holder.timer_start_btn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            when (timer.status) {
                TimerListItem.ADDED -> {
                    holder.timer_countdown.start(timer.startTime)

                    holder.timer_stop_btn.visibility = View.VISIBLE
                    holder.timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING
                    db.updateOneTimerInDatabase(timer, position)
                }
                TimerListItem.FREEZED -> {

                    holder.timer_countdown.start(timer.currentTime)

                    holder.timer_stop_btn.visibility = View.VISIBLE
                    holder.timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING
                    db.updateOneTimerInDatabase(timer, position)
                }
                TimerListItem.RUNNING -> {
                    holder.timer_countdown.start(timer.startTime)

                    holder.timer_stop_btn.visibility = View.VISIBLE
                    holder.timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING
                    db.updateOneTimerInDatabase(timer, position)
                }
                TimerListItem.RESETED -> {
                    holder.timer_countdown.start(timer.startTime)

                    holder.timer_stop_btn.visibility = View.VISIBLE
                    holder.timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING
                    db.updateOneTimerInDatabase(timer, position)


                }
            }


        }
        holder.timer_reset_btn.setOnClickListener {
            holder.timer_countdown.stop()

            timer.status = TimerListItem.RESETED
            holder.timer_stop_btn.visibility = View.VISIBLE
            holder.timer_reset_btn.visibility = View.INVISIBLE
            db.updateOneTimerInDatabase(timer, position)

            resetItem(timer, position)
            holder.timer_countdown.updateShow(timer.startTime)
        }
        holder.timer_stop_btn.setOnClickListener {
            holder.timer_countdown.stop()

            timer.currentTime = holder.timer_countdown.remainTime

            timer.status = TimerListItem.FREEZED
            holder.timer_stop_btn.visibility = View.INVISIBLE
            holder.timer_reset_btn.visibility = View.VISIBLE
            db.updateOneTimerInDatabase(timer, position)


        }


        checkStatus(holder, position)

        createPopUpMenu(holder, position)

    }

    override fun getItemCount() = timers.size

    private fun checkStatus(holder: ViewHolder, position: Int) {
        // Updating format from database. 0 = false, 1 = true
        val timer = timers[position]

        when (timer.status) {
            TimerListItem.ADDED -> {
                holder.timer_countdown.updateShow(timer.startTime)
            }
            TimerListItem.FREEZED -> {
                holder.timer_countdown.stop()
                holder.timer_countdown.updateShow(timers[position].currentTime)
                holder.timer_stop_btn.visibility = View.INVISIBLE
                holder.timer_reset_btn.visibility = View.VISIBLE

            }

            TimerListItem.RUNNING -> {
                holder.timer_countdown.start(timers[position].remainTime - System.currentTimeMillis())
                holder.timer_stop_btn.visibility = View.VISIBLE
                holder.timer_reset_btn.visibility = View.INVISIBLE

            }
            TimerListItem.RESETED -> {
                //TODO: Тут доделать
                holder.timer_countdown.stop()
                holder.timer_countdown.updateShow(timers[position].startTime)

                holder.timer_stop_btn.visibility = View.VISIBLE
                holder.timer_reset_btn.visibility = View.INVISIBLE

            }
        }

    }

    private fun resetItem(timer: TimerListItem, position: Int) {
        timer.currentTime = timer.startTime
        timer.remainTime = timer.startTime
        timer.status = TimerListItem.RESETED

        val cv = ContentValues()
        cv.put(DBHelper.START_TIME_COL, timer.startTime)
        cv.put(DBHelper.CURRENT_TIME_COL, timer.startTime)
        cv.put(DBHelper.REMAIN_TIME_COl, timer.startTime)
        cv.put(DBHelper.IS_FREEZE_COl, timer.status)

        val id = db.getDatabaseItemIdByRecyclerViewItemId(position)
        wdb.update(DBHelper.TIMER_TABLE, cv, "id = ?", arrayOf(id.toString()))


    }

    fun removeItem(position: Int) {
        timers.removeAt(position)

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, timers.size)

    }

    private fun createPopUpMenu(holder: ViewHolder, position: Int) {
        holder.timer_three_dots_menu.setOnClickListener {
            val menu = PopupMenu(holder.itemView.context, holder.itemView.timer_three_dots_menu)
            val inflater = menu.menuInflater
            inflater.inflate(R.menu.timer_list_item_menu, menu.menu)
            menu.setOnMenuItemClickListener { menu_item ->
                when (menu_item.itemId) {
                    R.id.menu_item_delete -> {
                        removeItem(position)
                        db.removeById(position)
                    }

                }
                return@setOnMenuItemClickListener true
            }
            menu.show()
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timer_stop_btn = itemView.findViewById<ImageButton>(R.id.timer_stop_btn)
        val timer_reset_btn = itemView.findViewById<ImageButton>(R.id.timer_reset_btn)
        val timer_start_btn = itemView.findViewById<ImageButton>(R.id.timer_start_btn)
        val timer_countdown = itemView.findViewById<CountdownView>(R.id.timer_countdown)
        val timer_three_dots_menu = itemView.findViewById<ImageButton>(R.id.timer_three_dots_menu)
        val timer_countDown = itemView.findViewById<CountdownView>(R.id.timer_countdown)

        val foreground = itemView.findViewById<ConstraintLayout>(R.id.foreground)

    }
}