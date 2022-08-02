package com.pandacorp.timeui.ui.stopwatch

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.R
import java.util.*


class StopWatchFragment : Fragment() {
    private val TAG = "MyLogs"

    private lateinit var root: View
    private lateinit var stopwatchStartBtn: Button
    private lateinit var stopwatchStopBtn: Button
    private lateinit var stopwatchResetBtn: Button
    private lateinit var stopwatchTextview: TextView

    private lateinit var timer: CountDownTimer

    private lateinit var sp: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private var seconds: Long = 0L

    //    private lateinit var running: Boolean
    private var running = false
    private var stopped = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_stopwatch, container, false)

        initViews()

        //Getting seconds from shared preferences
        seconds = sp.getLong("seconds", 0)

        //Reinstall stopwatch when open app and stop it if stop button was clicked
        stopped = sp.getBoolean("stopped", false)
        if (stopped) {
            //Without this text will be like 9 but not 00:00:09
            val hours = seconds / 3600
            val minutes = seconds % 3600 / 60
            val secs = seconds % 60
            val time: String = java.lang.String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                secs
            )
            Log.d(TAG, "onCreateView: seconds = $seconds")
            Log.d(TAG, "onCreateView: secs = $secs")

            stopwatchTextview.text = time
        }


        running = sp.getBoolean("running", false)
        if (running) {
            //Reinstall stopwatch when open app and add seconds when app wasn't active
            seconds += ((System.currentTimeMillis() -
                    sp.getLong("TimeInMillis", System.currentTimeMillis()))
                    / 1000).toLong()

            timer.cancel()
            timer.start()

        }


        return root

    }

    private fun initViews() {
        stopwatchStartBtn = root.findViewById(R.id.stopwatch_start_btn)
        stopwatchStopBtn = root.findViewById(R.id.stopwatch_stop_btn)
        stopwatchResetBtn = root.findViewById(R.id.stopwatch_reset_btn)
        stopwatchTextview = root.findViewById(R.id.stopwatch_textview)

        //Creating shared preferences objects
        sp = activity?.getSharedPreferences("StopWatch_SP", Context.MODE_PRIVATE)!!
        edit = sp.edit()

        //Setting stopwatch buttons OnClickListener
        stopwatchStartBtn.setOnClickListener { startStopWatch() }
        stopwatchStopBtn.setOnClickListener { stopStopWatch() }
        stopwatchResetBtn.setOnClickListener { resetStopWatch() }

        initTimer()


    }


    private fun startStopWatch() {
        running = true
        stopped = false

        timer.cancel()
        timer.start()


    }

    private fun stopStopWatch() {
        running = false
        stopped = true
        timer.cancel()
        //It will increase by one seconds value so here I decrease by one.
        seconds--
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        val secs = seconds % 60
        val time: String = java.lang.String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            hours,
            minutes,
            secs
        )

        stopwatchTextview.text = time

    }

    private fun resetStopWatch() {
        running = false
        stopped = true
        timer.cancel()
        seconds = 0

        stopwatchTextview.text = resources.getString(R.string.start_time)
    }


    private fun initTimer() {
        timer = object : CountDownTimer(1000_000_000_000_00, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60
                val time: String = java.lang.String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d",
                    hours,
                    minutes,
                    secs
                )

                stopwatchTextview.text = time
                seconds++


            }

            override fun onFinish() {

            }
        }
    }

    override fun onDestroy() {
        timer.cancel()
        edit.putLong("TimeInMillis", System.currentTimeMillis())
        edit.putLong("seconds", seconds)
        edit.putBoolean("running", running)
        edit.putBoolean("stopped", stopped)
        edit.apply()

        super.onDestroy()


    }
}