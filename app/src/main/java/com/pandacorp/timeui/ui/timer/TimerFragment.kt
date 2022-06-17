package com.pandacorp.timeui.ui.timer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {
    val TAG = "MyLogs"
    private lateinit var binding: FragmentTimerBinding
    private var hours = 0
    private var minutes = 0
    private var seconds = 0

    private var time: Long = 0

    private lateinit var sp: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

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
        sp = activity?.getSharedPreferences("Timer_SP", Context.MODE_PRIVATE)!!
        edit = sp.edit()

        //Reinstall the timer when open the app
        time = sp.getLong("Remain_time", 0) - System.currentTimeMillis()
        val FreezeTime = sp.getBoolean("FreezeTime", false)
        Log.d(TAG, "initViews: VAR FreezeTime = $FreezeTime")
        when (FreezeTime){
            true -> {
                binding.timerCountdown.updateShow(sp.getLong("Current_time", 0))
                Log.d(TAG, "initViews: FreezeTime = true")
            }

            false -> {
                binding.timerCountdown.start(time)
                Log.d(TAG, "initViews: FreezeTime = false")
            }
        }

        binding.timerTimepicker.setIs24HourView(true)
        binding.timerTimepicker.currentHour = 0
        binding.timerTimepicker.currentMinute = 0
        binding.timerTimepicker.setCurrentSecond(0)
        binding.timerStartBtn.setOnClickListener {
            when (FreezeTime){
                true -> {
                    binding.timerCountdown.start(sp.getLong("Current_time", 0))
                    edit.putBoolean("FreezeTime", false)
                    Log.d(TAG, "initViews: FreezeTime = true")

                }


                false -> {
                    hours = binding.timerTimepicker.currentHour
                    minutes = binding.timerTimepicker.currentMinute
                    seconds = binding.timerTimepicker.currentSeconds
                    //Setting time in hours, minutes, seconds to time in Milliseconds
                    val hoursInMilliseconds: Long = (hours * 60 * 60 * 1000).toLong()
                    val minutesInMilliseconds: Long = (minutes * 60 * 1000).toLong()
                    val secondsInMilliseconds: Long = (seconds * 1000).toLong()
                    time =
                        hoursInMilliseconds + minutesInMilliseconds + secondsInMilliseconds

                    binding.timerCountdown.start(time)
//                    edit.putBoolean("FreezeTime", true)
                    Log.d(TAG, "initViews: FreezeTime = false")

                }

            }



        }
        binding.timerStopBtn.setOnClickListener {
            binding.timerCountdown.stop()
            edit.putBoolean("FreezeTime", true)


        }
        binding.timerResetBtn.setOnClickListener {
            binding.timerCountdown.stop()
            binding.timerCountdown.start(100)
            binding.timerCountdown.allShowZero()

        }

    }


    override fun onDestroy() {
        edit.putLong("Current_time", binding.timerCountdown.remainTime)
        edit.putLong(
            "Remain_time",
            System.currentTimeMillis() + binding.timerCountdown.remainTime
        )
        edit.apply()


        super.onDestroy()

    }
}