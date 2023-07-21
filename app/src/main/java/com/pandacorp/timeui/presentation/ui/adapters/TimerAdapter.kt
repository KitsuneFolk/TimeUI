package com.pandacorp.timeui.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ItemTimerBinding
import com.pandacorp.timeui.domain.models.TimerItem

class TimerAdapter : ListAdapter<TimerItem, TimerAdapter.ViewHolder>(TimerDiffCallback()) {
    var timerListener: TimerListener? = null

    interface TimerListener {
        fun onTimerRemove(viewHolder: TimerAdapter.ViewHolder) // Remove in Room

        fun onTimerUpdate(position: Int, timerItem: TimerItem) // Update in Room

        fun onTimerClicked(position: Int, timerItem: TimerItem)
    }

    class TimerDiffCallback : DiffUtil.ItemCallback<TimerItem>() {
        override fun areItemsTheSame(oldItem: TimerItem, newItem: TimerItem): Boolean =
            newItem.id == oldItem.id

        override fun areContentsTheSame(oldItem: TimerItem, newItem: TimerItem): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(val binding: ItemTimerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timerItem: TimerItem) {
            checkStatus(binding, timerItem)

            binding.countdown.setOnClickListener {
                timerListener!!.onTimerClicked(bindingAdapterPosition, timerItem)
            }

            binding.startButton.setOnClickListener {
                when (timerItem.status) {
                    TimerItem.RUNNING -> {
                        binding.stopButton.visibility = View.VISIBLE
                        binding.resetButton.visibility = View.GONE

                        return@setOnClickListener
                    }

                    TimerItem.ADDED ->
                        binding.countdown.start(timerItem.startTime)

                    TimerItem.STOPPED ->
                        binding.countdown.start(timerItem.currentTime)

                    TimerItem.RESET ->
                        binding.countdown.start(timerItem.startTime)
                }
                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE

                timerItem.apply {
                    currentTime = binding.countdown.milliseconds + System.currentTimeMillis()
                    status = TimerItem.RUNNING
                }

                timerListener!!.onTimerUpdate(bindingAdapterPosition, timerItem)
            }

            binding.resetButton.setOnClickListener {
                binding.countdown.cancel()

                timerItem.status = TimerItem.RESET
                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE

                timerItem.currentTime = timerItem.startTime
                timerItem.status = TimerItem.RESET

                binding.countdown.updateShow(timerItem.startTime)

                timerListener!!.onTimerUpdate(bindingAdapterPosition, timerItem)
            }

            binding.stopButton.setOnClickListener {
                binding.countdown.cancel()

                binding.stopButton.visibility = View.GONE
                binding.resetButton.visibility = View.VISIBLE

                timerItem.apply {
                    status = TimerItem.STOPPED
                    currentTime = binding.countdown.milliseconds
                }

                timerListener!!.onTimerUpdate(bindingAdapterPosition, timerItem)
            }

            binding.menu.setOnClickListener {
                val menu = PopupMenu(binding.root.context, binding.menu)
                val inflater = menu.menuInflater
                inflater.inflate(R.menu.item_list_menu, menu.menu)
                menu.setOnMenuItemClickListener { menu_item ->
                    when (menu_item.itemId) {
                        R.id.menu_item_delete ->
                            timerListener!!.onTimerRemove(this)
                    }
                    return@setOnMenuItemClickListener true
                }
                menu.show()
            }
        }

    }

    override fun submitList(list: MutableList<TimerItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun checkStatus(binding: ItemTimerBinding, timerItem: TimerItem) {
        binding.countdown.cancel()
        binding.countdown.allShowZero()

        when (timerItem.status) {
            TimerItem.ADDED -> binding.countdown.updateShow(timerItem.startTime)

            TimerItem.STOPPED -> {
                binding.countdown.updateShow(timerItem.currentTime)

                binding.stopButton.visibility = View.GONE
                binding.resetButton.visibility = View.VISIBLE
            }

            TimerItem.RUNNING -> {
                binding.countdown.start(timerItem.currentTime - System.currentTimeMillis())

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }

            TimerItem.RESET -> {
                binding.countdown.updateShow(timerItem.startTime)

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }
        }
    }
}