package com.pandacorp.timeui.presentation.ui.stopwatch.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.CountryItemBinding
import com.pandacorp.timeui.domain.models.ClockItem
import com.pandacorp.timeui.presentation.ui.clock.ClockFragment
import java.text.SimpleDateFormat
import java.util.*

class CountryAdapter(private var context: Context) :
    ListAdapter<ClockItem, CountryAdapter.ViewHolder>(ClockDiffCallback()) {
    @ColorInt
    private var selectionColor: Int? = null
    
    companion object {
        private const val TAG = ClockFragment.TAG
    }
    
    var selectedPosition = RecyclerView.NO_POSITION
    
    class ClockDiffCallback : DiffUtil.ItemCallback<ClockItem>() {
        override fun areItemsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem == newItem
    }
    
    inner class ViewHolder(val binding: CountryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clockItem: ClockItem) {
            val formatter = SimpleDateFormat()
            val timeZone = TimeZone.getTimeZone(clockItem.timeZoneId)
            formatter.timeZone = timeZone
            Log.d(
                    TAG,
                    "bind: $adapterPosition, timeZone = ${clockItem.timeZoneId}, name = ${timeZone.displayName}")
            
            binding.countryTv.text = timeZone.displayName
            Log.d(TAG, "bind: countryTv.text = ${binding.countryTv.text}")
            
            if (selectedPosition == adapterPosition) {
                binding.countryItemRoot.setCardBackgroundColor(selectionColor!!)
            } else {
                // Unselect item
                binding.countryItemRoot.setCardBackgroundColor(Color.TRANSPARENT)
            }
            
            binding.countryItemRoot.setOnClickListener {
                val previousSelectedPosition =
                    selectedPosition
                
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun getItemCount(): Int = currentList.size
    
    override fun submitList(list: List<ClockItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CountryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Get colorPrimaryDark from themes for selection
        val tv = TypedValue()
        // context.theme.resolveAttribute(R.attr.selectionColor, tv, false)
        context.theme.resolveAttribute(R.attr.selectionColor, tv, true)
        selectionColor = tv.data
        
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clockItem = getItem(position)
        holder.bind(clockItem)
    }
    
}