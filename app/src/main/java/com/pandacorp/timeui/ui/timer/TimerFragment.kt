package com.pandacorp.timeui.ui.timer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {
    val TAG = "MyLogs"
    private lateinit var binding: FragmentTimerBinding
    private var hours = 0
    private var minutes = 0
    private var seconds = 0

    private var time: Long = 0

    private var isStop = true

    lateinit var sp: SharedPreferences
    lateinit var edit: SharedPreferences.Editor


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding init
        binding = FragmentTimerBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        //Creating sharedPreferences objects
        sp = activity?.getSharedPreferences("Remain_time_SP", Context.MODE_PRIVATE)!!
        edit = sp.edit()

        //Reset the countdown when opening the app
        time = sp.getLong("Remain_time", 0) - System.currentTimeMillis()
        Log.d(TAG, "initViews: isStop = $isStop")

        binding.timerCountdown.start(time)

        binding.timerTimepicker.setIs24HourView(true)
        binding.timerTimepicker.currentHour = 0
        binding.timerTimepicker.currentMinute = 0
        binding.timerTimepicker.setCurrentSecond(0)
        binding.timerStartBtn.setOnClickListener {
            hours = binding.timerTimepicker.currentHour
            minutes = binding.timerTimepicker.currentMinute
            seconds = binding.timerTimepicker.currentSeconds
            //Setting time in hours, minutes, seconds to time in Milliseconds
            val hoursInMilliseconds: Long = (hours * 60 * 60 * 1000).toLong()
            val minutesInMilliseconds: Long = (minutes * 60 * 1000).toLong()
            val secondsInMilliseconds: Long = (seconds * 1000).toLong()
            time =
                hoursInMilliseconds + minutesInMilliseconds + secondsInMilliseconds

            binding.timerStopResetBtn.text = resources.getString(R.string.stop_btn)
            binding.timerCountdown.start(time)


        }
        binding.timerStopResetBtn.setOnClickListener {
            if (isStop) {
                isStop = false
                binding.timerCountdown.stop()
                binding.timerStopResetBtn.text = resources.getString(R.string.reset_btn)
                Log.d(TAG, "initViews: stop")

            } else {
                isStop = true
                binding.timerCountdown.allShowZero()
                binding.timerCountdown.restart()
                //Clearing the SharedPreference value
                edit.putLong("Remain_time", 0)
                Log.d(TAG, "initViews: reset")
            }

        }


    }

    override fun onDestroy() {
        edit.putLong("Current_time", System.currentTimeMillis())
        edit.putLong(
            "Remain_time",
            System.currentTimeMillis() + binding.timerCountdown.remainTime
        )
        edit.apply()


        super.onDestroy()

    }
}