package com.pandacorp.timeui.ui.timer

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentTimerBinding
import com.pandacorp.timeui.ui.DBHelper


class TimerFragment : Fragment() {
    val TAG = "MyLogs"
    private lateinit var binding: FragmentTimerBinding
    private var hours = 0
    private var minutes = 0
    private var seconds = 0

    private var time: Long = 0
    private var remainTime: Long = 0
    private var currentTime: Long = 0
    private var isShowStop: Int = 1
    private var isFreeze: Int = 0

    private val contentValues = ContentValues()

    private var itemListCurrentTime = arrayListOf<Long>()
    private var itemListRemainTime = arrayListOf<Long>()
    private var itemListIsFreeze = arrayListOf<Int>()
    private var itemListIsShowStop = arrayListOf<Int>()


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
        //Creating DBHelper object
        val db = DBHelper(requireContext(), null)

        //Uploading the countdown when opening the app
        val cursor = db.getName(DBHelper.TIMER_TABLE)
        cursor!!.moveToFirst()
        val remainTime_col = cursor.getColumnIndex(DBHelper.REMAIN_TIME_COl)
        val currentTime_col = cursor.getColumnIndex(DBHelper.CURRENT_TIME_COL)
        val isShowStop_col = cursor.getColumnIndex(DBHelper.IS_SHOW_STOP_COL)
        val isFreeze_col = cursor.getColumnIndex(DBHelper.IS_FREEZE_COl)
        while (cursor.moveToNext()) {
            itemListRemainTime.add(cursor.getLong(remainTime_col) - System.currentTimeMillis());
            itemListCurrentTime.add(cursor.getLong(currentTime_col));
            itemListIsFreeze.add(cursor.getInt(isFreeze_col))
            itemListIsShowStop.add(cursor.getInt(isShowStop_col))
            remainTime = cursor.getLong(remainTime_col)
            currentTime = cursor.getLong(currentTime_col)
            isFreeze = cursor.getInt(isFreeze_col)
            isShowStop = cursor.getInt(isShowStop_col)
        }

        Log.d(TAG, "initViews: time = $time")

        //Checking if the time is stopped to set it
        try {
            if (isFreeze == 1) {
                // 0 = false
                // 1 = true
                binding.timerCountdown.stop()
                binding.timerCountdown.updateShow(currentTime.toLong())
                Log.d(TAG, "initViews: isFreeze = true")

            } else {
                // Without itemListRemainTime.lastIndex it won't work
                binding.timerCountdown.start(remainTime)
                Log.d(TAG, "initViews: isFreeze = false")
                //TODO: При повторном заходе обнуляется таймер
                // When app is loading first time

            }

        } catch (e: Exception) {
            //When app is starting first time
        }

        cursor.close()

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
//            contentValues.put(DBHelper.TIME_COl, time) TODO: Нужно ли это выражение?
            contentValues.put(DBHelper.IS_SHOW_STOP_COL, false)


        }
        binding.timerStopResetBtn.setOnClickListener {
            //TODO: Было бы не плохо улучшить логику получения результата при нажатии на определённый элемент списка
            if (isShowStop == 0) {
                binding.timerCountdown.stop()
                binding.timerStopResetBtn.text = resources.getString(R.string.reset_btn)
                Log.d(TAG, "initViews: stop")
                val contentValues = ContentValues()
                contentValues.put(DBHelper.IS_SHOW_STOP_COL, true)
                contentValues.put(DBHelper.IS_FREEZE_COl, 1)
                contentValues.put(DBHelper.REMAIN_TIME_COl, 0L)


            } else {
                //TODO: Доработать нажатие на кнопку reset
                binding.timerCountdown.allShowZero()
                binding.timerCountdown.restart()
                //Clearing the SharedPreference value
                Log.d(TAG, "initViews: reset")
                contentValues.put(DBHelper.IS_SHOW_STOP_COL, 0)
                contentValues.put(DBHelper.IS_FREEZE_COl, 1)
                contentValues.put(DBHelper.REMAIN_TIME_COl, 0L)


            }

        }

    }


    override fun onDestroy() {

        val db = DBHelper(requireContext(), null)
        val wdb = db.writableDatabase
        contentValues.put(DBHelper.CURRENT_TIME_COL, binding.timerCountdown.remainTime)
        contentValues.put(
            DBHelper.REMAIN_TIME_COl,
            System.currentTimeMillis() + binding.timerCountdown.remainTime
        )

        wdb.replace(DBHelper.TIMER_TABLE, DBHelper.CURRENT_TIME_COL + "=?", contentValues)

        Log.d(TAG, "onDestroy: CURRENT_TIME_COL = ${binding.timerCountdown.remainTime}")
        Log.d(
            TAG,
            "onDestroy: REMAIN_TIME_COl = ${System.currentTimeMillis() + binding.timerCountdown.remainTime}"
        )

        super.onDestroy()

    }
}