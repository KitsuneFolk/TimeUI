package com.pandacorp.timeui.presentation.ui.clocks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.databinding.FragmentClockBinding
import com.pandacorp.timeui.presentation.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class ClockFragment : Fragment() {
    companion object {
        const val TAG = "ClockFragment"
    }
    
    private var _binding: FragmentClockBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Utils.setupExceptionHandler()
        //Setting the app name for the action_bar
        _binding = FragmentClockBinding.inflate(inflater)
        initViews()
        
        return binding.root
    }
    
    @SuppressLint("SimpleDateFormat")
    private fun initViews() {
        val pattern = when (Locale.getDefault()) {
            Locale.US -> "MM.dd.yyyy"
            Locale.CHINA -> "yyyy.dd.MM"
            Locale.JAPAN -> "yyyy.dd.MM"
            else -> "dd.MM.yyyy"
        }
    
        val dateString = SimpleDateFormat(pattern).format(Date())
        binding.dateTv.text = dateString
    
        val dayString = SimpleDateFormat("EEEE").format(Date())
        binding.dayTv.text = dayString
    
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    
}
