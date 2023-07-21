package com.pandacorp.timeui.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ItemStopwatchBinding
import com.pandacorp.timeui.domain.models.StopwatchItem

class StopwatchAdapter : ListAdapter<StopwatchItem, StopwatchAdapter.ViewHolder>(StopwatchDiffCallback()) {

    interface StopwatchListener {
        fun onStopwatchRemove(position: Int)

        fun onStopwatchUpdate(stopwatchItem: StopwatchItem)

        fun onStopwatchClicked(stopwatchItem: StopwatchItem)
    }

    var stopwatchListener: StopwatchListener? = null

    inner class ViewHolder(val binding: ItemStopwatchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stopwatchItem: StopwatchItem) {
            binding.stopwatchView.setOnClickListener {
                stopwatchListener!!.onStopwatchClicked(stopwatchItem)
            }

            binding.startButton.setOnClickListener {
                when (stopwatchItem.status) {
                    StopwatchItem.RUNNING -> {
                        binding.stopButton.visibility = View.VISIBLE
                        binding.resetButton.visibility = View.GONE

                        return@setOnClickListener
                    }

                    StopwatchItem.ADDED ->
                        binding.stopwatchView.start()

                    StopwatchItem.STOPPED ->
                        binding.stopwatchView.start(stopwatchItem.stopTime)

                    StopwatchItem.RESET ->
                        binding.stopwatchView.start()
                }
                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE

                stopwatchItem.apply {
                    status = StopwatchItem.RUNNING
                    startSysTime = System.currentTimeMillis() - stopTime
                }
                stopwatchListener!!.onStopwatchUpdate(stopwatchItem)
            }
            binding.stopButton.setOnClickListener {
                binding.stopwatchView.cancel()

                stopwatchItem.apply {
                    stopTime = binding.stopwatchView.getTime()
                    status = StopwatchItem.STOPPED
                }
                stopwatchListener!!.onStopwatchUpdate(stopwatchItem)

                binding.stopButton.visibility = View.GONE
                binding.resetButton.visibility = View.VISIBLE
            }
            binding.resetButton.setOnClickListener {
                binding.stopwatchView.cancel()

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE

                stopwatchItem.apply {
                    startSysTime = StopwatchItem.START_TIME
                    stopTime = StopwatchItem.START_TIME
                    status = StopwatchItem.RESET
                }

                binding.stopwatchView.setTime(StopwatchItem.START_TIME)

                stopwatchListener!!.onStopwatchUpdate(stopwatchItem)
            }

            binding.menu.setOnClickListener {
                val menu = PopupMenu(itemView.context, binding.menu)
                menu.menuInflater.inflate(R.menu.item_list_menu, menu.menu)
                menu.setOnMenuItemClickListener { menu_item ->
                    when (menu_item.itemId) {
                        R.id.menu_item_delete -> stopwatchListener!!.onStopwatchRemove(bindingAdapterPosition)
                    }
                    return@setOnMenuItemClickListener true
                }
                menu.show()
            }

            checkStatus(binding, stopwatchItem)
        }
    }

    private class StopwatchDiffCallback : DiffUtil.ItemCallback<StopwatchItem>() {
        override fun areItemsTheSame(oldItem: StopwatchItem, newItem: StopwatchItem): Boolean =
            newItem.id == oldItem.id

        override fun areContentsTheSame(oldItem: StopwatchItem, newItem: StopwatchItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = ItemStopwatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<StopwatchItem>?) {
        val newList = list?.let { ArrayList(it) }
        super.submitList(newList)
    }

    private fun checkStatus(binding: ItemStopwatchBinding, stopwatchItem: StopwatchItem) {
        // Reset values
        binding.stopwatchView.cancel()
        binding.stopButton.visibility = View.VISIBLE
        binding.resetButton.visibility = View.GONE

        when (stopwatchItem.status) {
            StopwatchItem.ADDED -> {
                binding.stopwatchView.setTime(StopwatchItem.START_TIME)
            }

            StopwatchItem.STOPPED -> {
                binding.stopwatchView.setTime(stopwatchItem.stopTime)

                binding.stopButton.visibility = View.GONE
                binding.resetButton.visibility = View.VISIBLE
            }

            StopwatchItem.RUNNING -> {
                binding.stopwatchView.start(System.currentTimeMillis() - stopwatchItem.startSysTime)

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }

            StopwatchItem.RESET -> {
                binding.stopwatchView.setTime(StopwatchItem.START_TIME)

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }
        }
    }
}