package com.pandacorp.timeui.presentation.ui.stopwatch

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pandacorp.timeui.databinding.ActivityStopWatchBinding
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.PreferenceHandler

class StopWatchActivity : AppCompatActivity() {
    companion object {
        private const val TAG = StopWatchFragment.TAG
    }
    
    private var _binding: ActivityStopWatchBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var stopwatchItem: StopwatchItem
    private var position: Int = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceHandler(this).load()
        _binding = ActivityStopWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.stopwatchToolbarInclude.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        
        position = intent.getIntExtra(Constans.IntentItemPosition, -1)
        stopwatchItem = intent.getSerializableExtra(Constans.IntentItem) as StopwatchItem
        
        initViews()
        checkStatus()
        
    }
    
    private fun initViews() {
        binding.stopwatchActivityStartBtn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            when (stopwatchItem.status) {
                StopwatchItem.ADDED -> {
                    binding.stopwatchActivitySV.start()
                }
                StopwatchItem.STOPED -> {
                    binding.stopwatchActivitySV.start(stopwatchItem.stopTime)
                }
                StopwatchItem.RUNNING -> {
                    binding.stopwatchActivityStopBtn.visibility = View.VISIBLE
                    binding.stopwatchActivityResetBtn.visibility = View.GONE
                
                    return@setOnClickListener
                }
                StopwatchItem.RESETED -> {
                    binding.stopwatchActivitySV.start()
                }
            }
            binding.stopwatchActivityStopBtn.visibility = View.VISIBLE
            binding.stopwatchActivityResetBtn.visibility = View.GONE
            stopwatchItem.status = StopwatchItem.RUNNING
            stopwatchItem.startSysTime = System.currentTimeMillis() - stopwatchItem.stopTime
            setIntentResult()
        }
    
        binding.stopwatchActivityStopBtn.setOnClickListener {
            binding.stopwatchActivitySV.cancel()
        
            stopwatchItem.stopTime = binding.stopwatchActivitySV.getTime()
        
            stopwatchItem.status = StopwatchItem.STOPED
            binding.stopwatchActivityStopBtn.visibility = View.GONE
            binding.stopwatchActivityResetBtn.visibility = View.VISIBLE
            setIntentResult()
        }
    
        binding.stopwatchActivityResetBtn.setOnClickListener {
            binding.stopwatchActivitySV.cancel()
            binding.stopwatchActivitySV.setTime(StopwatchItem.START_TIME)
        
            binding.stopwatchActivityStopBtn.visibility = View.VISIBLE
            binding.stopwatchActivityResetBtn.visibility = View.GONE
        
            stopwatchItem.status = StopwatchItem.RESETED
        
            stopwatchItem.stopTime = StopwatchItem.START_TIME
            stopwatchItem.status = StopwatchItem.RESETED
            setIntentResult()
        }
        
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }
    
    private fun checkStatus() {
        when (stopwatchItem.status) {
            StopwatchItem.ADDED -> {
                binding.stopwatchActivitySV.setTime(StopwatchItem.START_TIME)
            }
            StopwatchItem.STOPED -> {
                binding.stopwatchActivitySV.cancel()
                binding.stopwatchActivitySV.setTime(stopwatchItem.stopTime)
                binding.stopwatchActivityStopBtn.visibility = View.GONE
                binding.stopwatchActivityResetBtn.visibility = View.VISIBLE
            
            }
        
            StopwatchItem.RUNNING -> {
                binding.stopwatchActivitySV.start(System.currentTimeMillis() - stopwatchItem.startSysTime)
                binding.stopwatchActivityStopBtn.visibility = View.VISIBLE
                binding.stopwatchActivityResetBtn.visibility = View.GONE
            
            }
            StopwatchItem.RESETED -> {
                binding.stopwatchActivitySV.cancel()
                binding.stopwatchActivitySV.setTime(StopwatchItem.START_TIME)
            
                binding.stopwatchActivityStopBtn.visibility = View.VISIBLE
                binding.stopwatchActivityResetBtn.visibility = View.GONE
            
            }
        }
    
    }
    
    private fun setIntentResult() {
        val i = Intent()
        i.putExtra(Constans.IntentItem, stopwatchItem)
        i.putExtra(Constans.IntentItemPosition, position)
        setResult(RESULT_OK, i)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }
    
    override fun onDestroy() {
        binding.stopwatchActivitySV.cancel()
        super.onDestroy()
        _binding = null
    }
}