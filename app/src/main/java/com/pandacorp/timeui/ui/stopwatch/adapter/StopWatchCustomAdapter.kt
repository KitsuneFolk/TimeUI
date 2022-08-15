package com.pandacorp.timeui.ui.stopwatch.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.ui.DBHelper
import com.pandacorp.timeui.ui.stopwatch.StopWatchActivity
import com.pandacorp.timeui.ui.stopwatch.Stopwatch
import com.pandacorp.timeui.ui.timer.adapter.TimerListItem
import kotlinx.android.synthetic.main.stopwatch_list_item.view.*
import org.jetbrains.anko.defaultSharedPreferences

class StopWatchCustomAdapter(
    private var context: Context,
    private var stopwatches: MutableList<TimerListItem>
) : RecyclerView.Adapter<StopWatchCustomAdapter.ViewHolder>() {
    private val TAG = "MyLogs"
    private val table = DBHelper.STOPWATCH_TABLE
    
    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Creating DBHelper object
        db = DBHelper(parent.context, null)
        
        //Creating WritableDatabase object
        wdb = db.writableDatabase
        
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.stopwatch_list_item, parent, false)
        return ViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stopwatch = stopwatches[position]
        holder.stopwatch_textView.setOnClickListener {
            val intent = Intent(context, StopWatchActivity::class.java)
            intent.putExtra("list_id", position)
            intent.putExtra("startTime", stopwatch.startTime)
            intent.putExtra("currentTime", stopwatch.currentTime)
            intent.putExtra("remainTime", stopwatch.remainTime)
            intent.putExtra("status", stopwatch.status)
            
            context.startActivity(intent)
        }
        holder.stopwatch_start_btn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            when (stopwatch.status) {
                TimerListItem.ADDED -> {
                    holder.stopwatch_stopwatch.start(stopwatch.startTime)
                    
                    holder.stopwatch_stop_btn.visibility = View.VISIBLE
                    holder.stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                    db.updateOneItemInDatabase(table, stopwatch, position)
                }
                TimerListItem.FREEZED -> {
                    
                    holder.stopwatch_stopwatch.start(stopwatch.currentTime)
                    
                    holder.stopwatch_stop_btn.visibility = View.VISIBLE
                    holder.stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                    db.updateOneItemInDatabase(table, stopwatch, position)
                }
                TimerListItem.RUNNING -> {
                    holder.stopwatch_stopwatch.start(stopwatch.startTime)
                    
                    holder.stopwatch_stop_btn.visibility = View.VISIBLE
                    holder.stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                    db.updateOneItemInDatabase(table, stopwatch, position)
                }
                TimerListItem.RESETED -> {
                    holder.stopwatch_stopwatch.start(stopwatch.startTime)
                    
                    holder.stopwatch_stop_btn.visibility = View.VISIBLE
                    holder.stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                    db.updateOneItemInDatabase(table, stopwatch, position)
                    
                    
                }
            }
            
            
        }
        holder.stopwatch_stop_btn.setOnClickListener {
            holder.stopwatch_stopwatch.stop()
            
            stopwatch.currentTime = holder.stopwatch_stopwatch.getTime()
            
            stopwatch.status = TimerListItem.FREEZED
            holder.stopwatch_stop_btn.visibility = View.INVISIBLE
            holder.stopwatch_reset_btn.visibility = View.VISIBLE
            db.updateOneItemInDatabase(table, stopwatch, position)
            
            
        }
        holder.stopwatch_reset_btn.setOnClickListener {
            holder.stopwatch_stopwatch.stop()
            
            stopwatch.status = TimerListItem.RESETED
            holder.stopwatch_stop_btn.visibility = View.VISIBLE
            holder.stopwatch_reset_btn.visibility = View.INVISIBLE
            db.updateOneItemInDatabase(table, stopwatch, position)
            
            resetItem(stopwatch, position)
            holder.stopwatch_stopwatch.setTime(stopwatch.startTime)
        }
        
        checkStatus(holder, position)
        
        createPopUpMenu(holder, position)
        
    }
    
    override fun getItemCount() = stopwatches.size
    
    fun removeItem(position: Int) {
        stopwatches.removeAt(position)
        
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, stopwatches.size)
        
    }
    
    
    private fun checkStatus(holder: StopWatchCustomAdapter.ViewHolder, position: Int) {
        // Updating format from database. 0 = false, 1 = true
        val stopwatch = stopwatches[position]
        
        when (stopwatch.status) {
            TimerListItem.ADDED -> {
                holder.stopwatch_stopwatch.setTime(stopwatch.startTime)
            }
            TimerListItem.FREEZED -> {
                holder.stopwatch_stopwatch.setTime(stopwatches[position].currentTime)
                holder.stopwatch_stop_btn.visibility = View.INVISIBLE
                holder.stopwatch_reset_btn.visibility = View.VISIBLE
                
            }
            
            TimerListItem.RUNNING -> {
                Log.d(TAG, "currentTime = ${stopwatch.currentTime}")
                Log.d(TAG, "remainTime = ${stopwatch.remainTime}")
                Log.d(TAG, "System.currentTimeMillis() = ${System.currentTimeMillis()}")
                val last_currentTimeMillis =
                    context.defaultSharedPreferences.getLong("stopwatch_currentTimeMillis", 0)
                holder.stopwatch_stopwatch.start(
                        stopwatch.currentTime + (
                                System.currentTimeMillis() - last_currentTimeMillis
                                ))
                holder.stopwatch_stop_btn.visibility = View.VISIBLE
                holder.stopwatch_reset_btn.visibility = View.INVISIBLE
    
            }
            TimerListItem.RESETED -> {
                holder.stopwatch_stopwatch.stop()
                holder.stopwatch_stopwatch.setTime(stopwatches[position].startTime)
                holder.stopwatch_stop_btn.visibility = View.VISIBLE
                holder.stopwatch_reset_btn.visibility = View.INVISIBLE
                
            }
        }
        
    }
    
    private fun resetItem(stopwatch: TimerListItem, position: Int) {
        stopwatch.currentTime = stopwatch.startTime
        stopwatch.remainTime = stopwatch.startTime
        stopwatch.status = TimerListItem.RESETED
        
        val cv = ContentValues()
        cv.put(DBHelper.START_TIME_COL, stopwatch.startTime)
        cv.put(DBHelper.CURRENT_TIME_COL, stopwatch.startTime)
        cv.put(DBHelper.REMAIN_TIME_COl, stopwatch.startTime)
        cv.put(DBHelper.IS_FREEZE_COl, stopwatch.status)
        
        val id = db.getDatabaseItemIdByRecyclerViewItemId(table, position)
        wdb.update(table, cv, "id = ?", arrayOf(id.toString()))
        
        
    }
    
    private fun createPopUpMenu(holder: StopWatchCustomAdapter.ViewHolder, position: Int) {
        holder.stopwatch_three_dots_menu.setOnClickListener {
            val menu = PopupMenu(holder.itemView.context, holder.itemView.stopwatch_three_dots_menu)
            val inflater = menu.menuInflater
            inflater.inflate(R.menu.timer_list_item_menu, menu.menu)
            menu.setOnMenuItemClickListener { menu_item ->
                when (menu_item.itemId) {
                    R.id.menu_item_delete -> {
                        removeItem(position)
                        db.removeById(DBHelper.STOPWATCH_TABLE, position)
                    }
                    
                }
                return@setOnMenuItemClickListener true
            }
            menu.show()
        }
        
    }
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seconds: Long = 0
        
        val stopwatch_stop_btn = itemView.findViewById<ImageButton>(R.id.stopwatch_stop_btn)
        val stopwatch_reset_btn = itemView.findViewById<ImageButton>(R.id.stopwatch_reset_btn)
        val stopwatch_start_btn = itemView.findViewById<ImageButton>(R.id.stopwatch_start_btn)
        val stopwatch_textView = itemView.findViewById<MaterialTextView>(R.id.stopwatch_textview)
        val stopwatch_stopwatch = Stopwatch(stopwatch_textView)
        val stopwatch_three_dots_menu =
            itemView.findViewById<ImageButton>(R.id.stopwatch_three_dots_menu)
        
        val foreground = itemView.findViewById<ConstraintLayout>(R.id.stopwatch_foreground)
        
        
    }
    
}