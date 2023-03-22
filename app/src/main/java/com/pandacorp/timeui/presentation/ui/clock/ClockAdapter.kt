package com.pandacorp.timeui.presentation.ui.clock

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ClockItemBinding
import com.pandacorp.timeui.domain.models.ClockItem
import java.text.SimpleDateFormat
import java.util.*

class ClockAdapter(private var context: Context) :
    ListAdapter<ClockItem, ClockAdapter.ViewHolder>(ClockDiffCallback()) {
    companion object {
        private const val TAG = ClockFragment.TAG
        
        private const val datePattern =
            "dd.MM.yyyy" // pattern to show date including day, month and year e.g 03.04.2023
        private const val dayPattern = "EEEE" // pattern to show full day name e.g Thursday
        
    }
    
    class ClockDiffCallback : DiffUtil.ItemCallback<ClockItem>() {
        override fun areItemsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem == newItem
    }
    
    inner class ViewHolder(val binding: ClockItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clockItem: ClockItem) {
            val locale = Locale.getDefault()
            val dateFormatter = SimpleDateFormat(datePattern, locale)
            val dayFormatter = SimpleDateFormat(dayPattern, locale)
            val timeZone = TimeZone.getTimeZone(clockItem.timeZoneId)
            val date = Calendar.getInstance(timeZone).time
            
            dateFormatter.timeZone = timeZone
            dayFormatter.timeZone = timeZone
            
            val dateString = dateFormatter.format(date)
            val dayString = dayFormatter.format(date)
            
            binding.textClock.timeZone = timeZone.id
            binding.dateTv.text = dateString
            binding.dayTv.text = dayString
            binding.countryTv.text = timeZone.displayName
            
            // Add font to the TextClock
            val font = ResourcesCompat.getFont(context, R.font.inter)
            binding.textClock.typeface = font
        }
    }
    
    override fun getItemCount(): Int = currentList.size
    
    override fun submitList(list: MutableList<ClockItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ClockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clockItem = getItem(position)
        holder.bind(clockItem)
    }
}