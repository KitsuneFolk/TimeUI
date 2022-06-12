package com.pandacorp.timeui.ui.stopwatch

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentStopwatchBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class StopWatchFragment : Fragment() {
    private lateinit var binding: FragmentStopwatchBinding
    private lateinit var timer: CountDownTimer

    private var time: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        initViews()

        return binding.root

    }

    private fun initViews() {

        binding.stopwatchStartBtn.setOnClickListener {
            startTimer()


        }
        binding.stopwatchStopBtn.setOnClickListener { stopTimer() }
        binding.stopwatchResetBtn.setOnClickListener { resetTimer() }

    }


    private fun resetTimer() {
        stopTimer()
        time = 0
        binding.stopwatchTextview.text =
            activity?.resources?.getText(R.string.start_time).toString()
    }


    private fun startTimer() {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 9999)
        timer = object : CountDownTimer(calendar.timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                this@StopWatchFragment.time++
                val hours = this@StopWatchFragment.time / (1000 * 60 * 60) % 24
                val minutes = this@StopWatchFragment.time / 1000 / 60
                val seconds = this@StopWatchFragment.time % 60
                val stringTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                binding.stopwatchTextview.text = stringTime

            }

            override fun onFinish() {

            }
        }

        timer.cancel()
        timer.start()



    }

    private fun stopTimer() {
        timer.cancel()
    }

}