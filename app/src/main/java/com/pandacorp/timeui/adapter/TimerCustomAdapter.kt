package com.pandacorp.timeui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.countdownview.CountdownView
import com.pandacorp.timeui.R

class TimerCustomAdapter(private val timers: List<Long>) : RecyclerView.Adapter<TimerCustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timer_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.timer_start_btn.setOnClickListener {
            holder.timer_countdown.start(timers[position])
        }
        holder.timer_stop_btn.setOnClickListener {
            holder.timer_countdown.stop()

        }
    }

    override fun getItemCount() = timers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val timer_stop_btn = itemView.findViewById<ImageButton>(R.id.timer_stop_btn)
        val timer_start_btn = itemView.findViewById<ImageButton>(R.id.timer_start_btn)
        val timer_countdown = itemView.findViewById<CountdownView>(R.id.timer_countdown)
    }
}