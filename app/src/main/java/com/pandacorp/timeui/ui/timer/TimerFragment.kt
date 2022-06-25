package com.pandacorp.timeui.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pandacorp.timeui.R
import com.pandacorp.timeui.adapter.TimerCustomAdapter

class TimerFragment : Fragment() {
    private val TAG = "MyLogs"

    private lateinit var root: View

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    private lateinit var customAdapter: TimerCustomAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_timer, container, false)

        initViews()

        return root
    }

    private fun initViews() {
        val timers = mutableListOf<Long>()
        (1..5).forEach { i -> timers.add(i.toLong() * 1000 * 60) }

        customAdapter = TimerCustomAdapter(timers)

        recyclerView = root.findViewById(R.id.timer_recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = customAdapter
        
        fab = root.findViewById(R.id.timer_add_fab)
        fab.setOnClickListener {
            timers.add(10 * 1000 * 60)
            customAdapter.notifyDataSetChanged()
        }
        
        
    }
//    override fun onDestroy() {
//        edit.putLong("Current_time", binding.timerCountdown.remainTime)
//        edit.putLong(
//            "Remain_time",
//            System.currentTimeMillis() + binding.timerCountdown.remainTime
//        )
//        edit.apply()
//
//
//        super.onDestroy()
//
//    }
}