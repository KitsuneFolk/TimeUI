package com.pandacorp.timeui.presentation.ui.stopwatch.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.presentation.ui.stopwatch.StopWatchActivity
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.StopwatchView

class StopwatchAdapter(private var context: Context) :
    ListAdapter<StopwatchItem, StopwatchAdapter.ViewHolder>(StopwatchDiffCallback()) {
    companion object {
        const val TAG = "StopwatchAdapter"
    }
    
    interface StopwatchListener {
        // Method where we remove stopwatch in room
        fun onStopwatchRemove(viewHolder: ViewHolder, position: Int)
        
        // Method where we update stopwatch in room
        fun onStopwatchUpdate(position: Int, stopwatchItem: StopwatchItem)
        
        // Method where we open StopwatchActivity
        fun onStopwatchClicked(): ActivityResultLauncher<Intent>
    }
    
    private class StopwatchDiffCallback : DiffUtil.ItemCallback<StopwatchItem>() {
        override fun areItemsTheSame(oldItem: StopwatchItem, newItem: StopwatchItem): Boolean {
            val result =
                (oldItem.uuid == newItem.uuid && newItem.startSysTime == oldItem.startSysTime
                        && oldItem.stopTime == newItem.stopTime && oldItem.status == newItem.status)
            return result
        }
        
        
        override fun areContentsTheSame(oldItem: StopwatchItem, newItem: StopwatchItem): Boolean {
            return oldItem == newItem
            
        }
    }
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopBtn = itemView.findViewById<ImageView>(R.id.stopwatchStopBtn)!!
        val resetBtn = itemView.findViewById<ImageView>(R.id.stopwatchResetBtn)!!
        val startBtn = itemView.findViewById<ImageView>(R.id.stopwatchStartBtn)!!
        val stopwatch = itemView.findViewById<StopwatchView>(R.id.stopwatchView)!!
        val threeDotsMenu = itemView.findViewById<ImageView>(R.id.stopwatchMenu)!!
        val foreground = itemView.findViewById<LinearLayout>(R.id.stopwatchForeground)!!
    }
    
    private var stopwatchListener: StopwatchListener? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.stopwatch_list_item, parent, false)
        return ViewHolder(itemView)
    }
    
    override fun getItemCount(): Int = currentList.size
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stopwatchItem = getItem(position)
        holder.stopwatch.setOnClickListener {
            val intent =
                Intent(context, StopWatchActivity::class.java)
            intent.putExtra(Constans.IntentItem, stopwatchItem)
            intent.putExtra(Constans.IntentItemPosition, holder.adapterPosition)
            
            stopwatchListener!!.onStopwatchClicked().launch(intent)
        }
        holder.startBtn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn is pressed,
            // start counting from stopTime,
            // if the reset btn is pressed then when start btn is pressed,
            // start counting from startTime
            
            when (stopwatchItem.status) {
                StopwatchItem.RUNNING -> {
                    holder.stopBtn.visibility = View.VISIBLE
                    holder.resetBtn.visibility = View.GONE
                    
                    return@setOnClickListener
                }
                StopwatchItem.ADDED -> {
                    holder.stopwatch.start()
                }
                StopwatchItem.STOPED -> {
                    holder.stopwatch.start(stopwatchItem.stopTime)
                }
                StopwatchItem.RESETED -> {
                    holder.stopwatch.start()
                }
            }
            holder.stopBtn.visibility = View.VISIBLE
            holder.resetBtn.visibility = View.GONE
            
            stopwatchItem.status = StopwatchItem.RUNNING
            stopwatchItem.startSysTime = System.currentTimeMillis() - stopwatchItem.stopTime
            stopwatchListener!!.onStopwatchUpdate(holder.adapterPosition, stopwatchItem)
        }
        holder.stopBtn.setOnClickListener {
            holder.stopwatch.cancel()
            
            stopwatchItem.stopTime = holder.stopwatch.getTime()
            stopwatchItem.status = StopwatchItem.STOPED
            stopwatchListener!!.onStopwatchUpdate(holder.adapterPosition, stopwatchItem)
            
            holder.stopBtn.visibility = View.GONE
            holder.resetBtn.visibility = View.VISIBLE
            
        }
        holder.resetBtn.setOnClickListener {
            holder.stopwatch.cancel()
            
            stopwatchItem.status = StopwatchItem.RESETED
            holder.stopBtn.visibility = View.VISIBLE
            holder.resetBtn.visibility = View.GONE
            
            stopwatchItem.stopTime = StopwatchItem.START_TIME
            stopwatchItem.startSysTime = StopwatchItem.START_TIME
            stopwatchItem.status = StopwatchItem.RESETED
            stopwatchListener!!.onStopwatchUpdate(holder.adapterPosition, stopwatchItem)
            
            holder.stopwatch.setTime(StopwatchItem.START_TIME)
        }
        
        checkStatus(holder, stopwatchItem)
        
        holder.threeDotsMenu.setOnClickListener {
            val menu = PopupMenu(holder.itemView.context, holder.threeDotsMenu)
            val inflater = menu.menuInflater
            inflater.inflate(R.menu.item_list_menu, menu.menu)
            menu.setOnMenuItemClickListener { menu_item ->
                when (menu_item.itemId) {
                    R.id.menu_item_delete -> {
                        stopwatchListener!!.onStopwatchRemove(holder, holder.adapterPosition)
                    }
            
                }
                return@setOnMenuItemClickListener true
            }
            menu.show()
        }
    }
    
    private fun checkStatus(holder: ViewHolder, stopwatchItem: StopwatchItem) {
        // Reset views visibility
        holder.stopBtn.visibility = View.VISIBLE
        holder.resetBtn.visibility = View.GONE
        
        when (stopwatchItem.status) {
            StopwatchItem.ADDED -> {
                holder.stopwatch.setTime(StopwatchItem.START_TIME)
            }
            StopwatchItem.STOPED -> {
                holder.stopwatch.setTime(stopwatchItem.stopTime)
                holder.stopBtn.visibility = View.GONE
                holder.resetBtn.visibility = View.VISIBLE
                
            }
            StopwatchItem.RUNNING -> {
                holder.stopwatch.start(System.currentTimeMillis() - stopwatchItem.startSysTime)
                
                holder.stopBtn.visibility = View.VISIBLE
                holder.resetBtn.visibility = View.GONE
            }
            StopwatchItem.RESETED -> {
                holder.stopwatch.cancel()
                holder.stopwatch.setTime(StopwatchItem.START_TIME)
                holder.stopBtn.visibility = View.VISIBLE
                holder.resetBtn.visibility = View.GONE
                
            }
        }
    }
    
    override fun submitList(list: MutableList<StopwatchItem>?) {
        val newList = list?.let { ArrayList(it) }
        super.submitList(newList)
    }
    
    fun setStopwatchListener(stopwatchListener: StopwatchListener) {
        this.stopwatchListener = stopwatchListener
    }
}