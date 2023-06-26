package com.pandacorp.timeui.presentation.utils.dialogs

import android.content.Context
import android.os.Bundle
import com.pandacorp.timeui.databinding.DialogTimePickerBinding

class DialogTimer(context: Context) : CustomDialog(context) {
    var onTimeAppliedListener: ((Int, Int, Int) -> Unit)? = null

    private var _binding: DialogTimePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogTimePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.timePicker.apply {
            currentMinute = 5
            currentHour = 0
            setIs24HourView(true)
            setCurrentSecond(0)
            setOnTimeChangedListener { _, _, _, _ ->
                vibrate()
            }
        }

        binding.ok.setOnClickListener {
            onTimeAppliedListener?.invoke(
                binding.timePicker.currentHour,
                binding.timePicker.currentMinute,
                binding.timePicker.currentSeconds
            )
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }
}