package com.pandacorp.timeui.presentation.ui.timer

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pandacorp.timeui.databinding.ActivityTimerBinding
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.PreferenceHandler

class TimerActivity : AppCompatActivity() {
    private var _binding: ActivityTimerBinding? = null
    private val binding get() = _binding!!
    
    private var position: Int = -1
    private lateinit var timerItem: TimerItem
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceHandler(this).load()
        _binding = ActivityTimerBinding.inflate(layoutInflater)
        setSupportActionBar(binding.timerToolbarInclude.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        setContentView(binding.root)
        
        position = intent.getIntExtra(Constans.IntentItemPosition, -1)
        timerItem = intent.getSerializableExtra(Constans.IntentItem) as TimerItem
        
        initViews()
        checkStatus()
        
        
    }
    
    private fun initViews() {
        binding.timerActivityStartBtn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            when (timerItem.status) {
                TimerItem.ADDED -> {
                    binding.timerActivityCountdown.start(timerItem.startTime)
                }
                TimerItem.STOPED -> {
                    binding.timerActivityCountdown.start(timerItem.currentTime)
                }
                TimerItem.RUNNING -> {
                    binding.timerActivityStopBtn.visibility = View.VISIBLE
                    binding.timerActivityResetBtn.visibility = View.GONE
                    return@setOnClickListener
                }
                TimerItem.RESETED -> {
                    binding.timerActivityCountdown.start(timerItem.startTime)
                }
            }
            binding.timerActivityStopBtn.visibility = View.VISIBLE
            binding.timerActivityResetBtn.visibility = View.GONE
            
            timerItem.currentTime =
                binding.timerActivityCountdown.remainTime + System.currentTimeMillis()
            timerItem.status = TimerItem.RUNNING
            setIntentResult()
        }
        
        binding.timerActivityStopBtn.setOnClickListener {
            binding.timerActivityCountdown.stop()
            
            timerItem.currentTime = binding.timerActivityCountdown.remainTime
            
            timerItem.status = TimerItem.STOPED
            binding.timerActivityStopBtn.visibility = View.GONE
            binding.timerActivityResetBtn.visibility = View.VISIBLE
            
            setIntentResult()
        }
        
        binding.timerActivityResetBtn.setOnClickListener {
            binding.timerActivityCountdown.stop()
            
            timerItem.status = TimerItem.RESETED
            timerItem.currentTime = timerItem.startTime
            binding.timerActivityStopBtn.visibility = View.VISIBLE
            binding.timerActivityResetBtn.visibility = View.GONE
            
            binding.timerActivityCountdown.updateShow(timerItem.startTime)
            
            setIntentResult()
        }
    }
    
    private fun checkStatus() {
        
        when (timerItem.status) {
            TimerItem.ADDED -> {
                binding.timerActivityCountdown.updateShow(timerItem.startTime)
            }
            TimerItem.STOPED -> {
                binding.timerActivityCountdown.stop()
                binding.timerActivityCountdown.updateShow(timerItem.currentTime)
                binding.timerActivityStopBtn.visibility = View.GONE
                binding.timerActivityResetBtn.visibility = View.VISIBLE
                
            }
            
            TimerItem.RUNNING -> {
                binding.timerActivityCountdown.start(timerItem.currentTime - System.currentTimeMillis())
                binding.timerActivityStopBtn.visibility = View.VISIBLE
                binding.timerActivityResetBtn.visibility = View.GONE
                
            }
            TimerItem.RESETED -> {
                binding.timerActivityCountdown.stop()
                binding.timerActivityCountdown.updateShow(timerItem.startTime)
                
                binding.timerActivityStopBtn.visibility = View.VISIBLE
                binding.timerActivityResetBtn.visibility = View.GONE
            }
        }
    }
    
    private fun setIntentResult() {
        val i = Intent()
        i.putExtra(Constans.IntentItem, timerItem)
        i.putExtra(Constans.IntentItemPosition, position)
        setResult(RESULT_OK, i)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onDestroy() {
        binding.timerActivityCountdown.stop()
        super.onDestroy()
        _binding = null
    }
    
    
}