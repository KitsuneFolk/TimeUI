package com.pandacorp.timeui.ui.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.databinding.FragmentTimerBinding
import kotlinx.coroutines.runBlocking


class TimerFragment : Fragment() {
    val TAG = "MyLogs"
    private lateinit var binding: FragmentTimerBinding
    private var hours = 0
    private var minutes = 0
    private var seconds = 0

    private lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        Log.d(TAG, "TimerFragment.initViews() called")
        binding.timerTimepicker.setIs24HourView(true)
        binding.timerTimepicker.currentHour = 0
        binding.timerTimepicker.currentMinute = 0
        binding.timerTimepicker.setCurrentSecond(0)
        binding.timerTimepicker.setOnTimeChangedListener { view, hourOfDay, minute, seconds ->
            val time = String.format("%02d:%02d:%02d", hourOfDay, minute, seconds)
            binding.timerTv.text = time

        }
        binding.timerStartBtn.setOnClickListener {
            hours = binding.timerTimepicker.currentHour
            minutes = binding.timerTimepicker.currentMinute
            seconds = binding.timerTimepicker.currentSeconds
            //Setting time in hours, minutes, seconds to time in Milliseconds
            val hoursInMilliseconds: Long = (hours * 60 * 60 * 1000).toLong()
            val minutesInMilliseconds: Long = (minutes * 60 * 1000).toLong()
            val secondsInMilliseconds: Long = (seconds * 1000).toLong()
            val timeInMilliseconds: Long =
                hoursInMilliseconds + minutesInMilliseconds + secondsInMilliseconds
            //Try...catch block is needed for cancel last timer if it exists
//            try {
//                timer.cancel()
//            } catch (e: Exception) {
//                Log.e(TAG, "initViews: $e")
//            }
            runBlocking {
                setTimer(timeInMilliseconds)

            }

        }
    }

    private fun setTimer(timeInMilliseconds: Long) {
        timer = object : CountDownTimer(timeInMilliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60) % 24
                val minutes = millisUntilFinished / 1000 / 60 % 60
                val seconds = millisUntilFinished / 1000 % 60
                val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                binding.timerTv.text = time
                Log.d(TAG, "onTick: " + time)
            }

            override fun onFinish() {

            }
        }

        timer.cancel()
        timer.start()


    }


}