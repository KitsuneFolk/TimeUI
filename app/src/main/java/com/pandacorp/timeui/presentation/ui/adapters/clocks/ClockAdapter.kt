package com.pandacorp.timeui.presentation.ui.adapters.clocks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.databinding.ItemClockBinding
import com.pandacorp.timeui.domain.models.ClockItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ClockAdapter : ListAdapter<ClockItem, ClockAdapter.ViewHolder>(ClockDiffCallback()) {
    companion object {
        private const val datePattern = "dd.MM.yyyy"
        private const val dayPattern = "EEEE" // Show the full day name e.g Thursday
    }

    class ClockDiffCallback : DiffUtil.ItemCallback<ClockItem>() {
        override fun areItemsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(val binding: ItemClockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clockItem: ClockItem) {
            val locale = Locale.getDefault()
            val dateFormatter = SimpleDateFormat(datePattern, locale)
            val dayFormatter = SimpleDateFormat(dayPattern, locale)
            val timeZone = TimeZone.getTimeZone(clockItem.timeZone)
            val date = Calendar.getInstance(timeZone).time

            dateFormatter.timeZone = timeZone
            dayFormatter.timeZone = timeZone

            val dateString = dateFormatter.format(date)
            val dayString = dayFormatter.format(date)

            binding.textClock.timeZone = timeZone.id
            binding.dateTv.text = dateString
            binding.dayTv.text = dayString
            binding.countryTextView.text = timeZone.displayName
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun submitList(list: MutableList<ClockItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemClockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clockItem = getItem(position)
        holder.bind(clockItem)
    }
}