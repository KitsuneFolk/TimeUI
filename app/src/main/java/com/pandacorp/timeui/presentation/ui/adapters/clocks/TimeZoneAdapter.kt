package com.pandacorp.timeui.presentation.ui.adapters.clocks

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ItemTimezoneBinding
import com.pandacorp.timeui.domain.models.ClockItem
import java.util.TimeZone

class TimeZoneAdapter : ListAdapter<ClockItem, TimeZoneAdapter.ViewHolder>(ClockDiffCallback()) {
    @ColorInt
    private var selectionColor: Int? = null

    var selectedPosition: Int? = null

    private class ClockDiffCallback : DiffUtil.ItemCallback<ClockItem>() {
        override fun areItemsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ClockItem, newItem: ClockItem): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(val binding: ItemTimezoneBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clockItem: ClockItem) {
            val timeZone = TimeZone.getTimeZone(clockItem.timeZone)
            binding.countryTextView.text = timeZone.displayName

            binding.root.apply {
                strokeColor =
                    if (selectedPosition == bindingAdapterPosition) selectionColor!! // Select
                    else Color.LTGRAY // Unselect
                setOnClickListener {
                    val previousSelectedPosition = selectedPosition ?: RecyclerView.NO_POSITION
                    selectedPosition = bindingAdapterPosition

                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(bindingAdapterPosition)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = currentList.size

    override fun submitList(list: List<ClockItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimezoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val tv = TypedValue()
        parent.context.theme.resolveAttribute(R.attr.selectionColor, tv, true)
        selectionColor = tv.data

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}