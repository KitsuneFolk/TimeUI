package com.pandacorp.timeui.presentation.ui.timer.adapter

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
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.presentation.ui.timer.TimerActivity
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.countdownview.CountdownView


class TimerAdapter(private var context: Context) :
    ListAdapter<TimerItem, TimerAdapter.ViewHolder>(TimerDiffCallback()) {
    companion object {
        const val TAG = "TimerAdapter"
    }
    
    interface TimerListener {
        fun onTimerRemove(
            viewHolder: ViewHolder,
            position: Int
        ) // method where we remove timer in room
        
        fun onTimerUpdate(
            position: Int,
            timerItem: TimerItem
        ) // method where we update timer in room
        
        fun onTimerClicked(): ActivityResultLauncher<Intent>
    }
    
    class TimerDiffCallback : DiffUtil.ItemCallback<TimerItem>() {
        override fun areItemsTheSame(oldItem: TimerItem, newItem: TimerItem): Boolean =
            (oldItem.uuid == newItem.uuid && newItem.currentTime == oldItem.currentTime
                    && oldItem.startTime == newItem.startTime && oldItem.status == newItem.status)
        
        override fun areContentsTheSame(oldItem: TimerItem, newItem: TimerItem): Boolean =
            oldItem == newItem
    }
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopBtn = itemView.findViewById<ImageView>(R.id.timer_stop_btn)!!
        val resetBtn = itemView.findViewById<ImageView>(R.id.timer_reset_btn)!!
        val startBtn = itemView.findViewById<ImageView>(R.id.timer_start_btn)!!
        val countdown = itemView.findViewById<CountdownView>(R.id.timer_countdown)!!
        val threeDotsMenu = itemView.findViewById<ImageView>(R.id.timer_three_dots_menu)!!
        
        val foreground = itemView.findViewById<LinearLayout>(R.id.timer_foreground)!!
        
    }
    
    private var timerListener: TimerListener? = null
    
    override fun getItemCount(): Int = currentList.size
    
    override fun submitList(list: MutableList<TimerItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timer_list_item, parent, false)
        return ViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timerItem = getItem(position)
        holder.countdown.setOnClickListener {
            val intent =
                Intent(context, TimerActivity::class.java)
            intent.putExtra(Constans.IntentItem, timerItem)
            intent.putExtra(Constans.IntentItemPosition, holder.adapterPosition)
            
            timerListener!!.onTimerClicked().launch(intent)
        }
        
        holder.startBtn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            when (timerItem.status) {
                TimerItem.RUNNING -> {
                    holder.stopBtn.visibility = View.VISIBLE
                    holder.resetBtn.visibility = View.GONE
                    
                    return@setOnClickListener
                }
                TimerItem.ADDED -> {
                    holder.countdown.start(timerItem.startTime)
                }
                TimerItem.STOPED -> {
                    holder.countdown.start(timerItem.currentTime)
                }
                TimerItem.RESETED -> {
                    holder.countdown.start(timerItem.startTime)
                }
            }
            holder.stopBtn.visibility = View.VISIBLE
            holder.resetBtn.visibility = View.GONE
            
            timerItem.currentTime = holder.countdown.remainTime + System.currentTimeMillis()
            timerItem.status = TimerItem.RUNNING
            
            timerListener!!.onTimerUpdate(holder.adapterPosition, timerItem)
            
        }
        
        holder.resetBtn.setOnClickListener {
            holder.countdown.stop()
            
            timerItem.status = TimerItem.RESETED
            holder.stopBtn.visibility = View.VISIBLE
            holder.resetBtn.visibility = View.GONE
            
            timerItem.currentTime = timerItem.startTime
            timerItem.status = TimerItem.RESETED
            
            holder.countdown.updateShow(timerItem.startTime)
            
            timerListener!!.onTimerUpdate(holder.adapterPosition, timerItem)
        }
        
        holder.stopBtn.setOnClickListener {
            holder.countdown.stop()
            
            holder.stopBtn.visibility = View.GONE
            holder.resetBtn.visibility = View.VISIBLE
    
            timerItem.status = TimerItem.STOPED
            timerItem.currentTime = holder.countdown.remainTime
    
            timerListener!!.onTimerUpdate(holder.adapterPosition, timerItem)
    
        }
    
        checkStatus(holder, timerItem)
    
        holder.threeDotsMenu.setOnClickListener {
            val menu = PopupMenu(holder.itemView.context, holder.threeDotsMenu)
            val inflater = menu.menuInflater
            inflater.inflate(R.menu.item_list_menu, menu.menu)
            menu.setOnMenuItemClickListener { menu_item ->
                when (menu_item.itemId) {
                    R.id.menu_item_delete -> {
                        timerListener?.onTimerRemove(
                                viewHolder = holder,
                                position = holder.adapterPosition)
                    }
                
                }
                return@setOnMenuItemClickListener true
            }
            menu.show()
        }
    
    }
    
    private fun checkStatus(holder: ViewHolder, timerItem: TimerItem) {
        when (timerItem.status) {
            TimerItem.ADDED -> {
                holder.countdown.updateShow(timerItem.startTime)
            }
            TimerItem.STOPED -> {
                holder.countdown.stop()
                holder.countdown.updateShow(timerItem.currentTime)
                holder.stopBtn.visibility = View.GONE
                holder.resetBtn.visibility = View.VISIBLE
                
            }
            TimerItem.RUNNING -> {
                holder.countdown.stop()
                holder.countdown.start(timerItem.currentTime - System.currentTimeMillis())
                holder.stopBtn.visibility = View.VISIBLE
                holder.resetBtn.visibility = View.GONE
                
            }
            TimerItem.RESETED -> {
                holder.countdown.stop()
                holder.countdown.updateShow(timerItem.startTime)
                
                holder.stopBtn.visibility = View.VISIBLE
                holder.resetBtn.visibility = View.GONE
                
            }
        }
        
    }
    
    fun setTimerListener(timerListener: TimerListener) {
        this.timerListener = timerListener
    }
}