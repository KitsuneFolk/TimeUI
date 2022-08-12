package com.pandacorp.timeui.ui.stopwatch

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import cn.iwgang.countdownview.CountdownView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.settings.MySettings
import com.pandacorp.timeui.settings.SettingsActivity
import com.pandacorp.timeui.ui.DBHelper
import com.pandacorp.timeui.ui.timer.adapter.TimerListItem

class StopWatchActivity : AppCompatActivity() {
    private val table = DBHelper.STOPWATCH_TABLE
    private lateinit var stopwatch_stop_btn: ImageButton
    private lateinit var stopwatch_reset_btn: ImageButton
    private lateinit var stopwatch_start_btn: ImageButton
    private lateinit var stopwatch_countdown: CountdownView
    
    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase
    
    private var list_id = -10
    private var startTime = -10L
    private var currentTime = -10L
    private var remainTime = -10L
    private var status = -10
    
    private lateinit var stopwatch: TimerListItem
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val mySettings = MySettings(this)
        mySettings.start()
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)
        
        initVars()
        initViews()
        checkStatus()
        
        
    }
    
    private fun initVars() {
        db = DBHelper(this, null)
        wdb = db.writableDatabase
        
        list_id = intent.getIntExtra("list_id", -10)
        startTime = intent.getLongExtra("startTime", -10)
        currentTime = intent.getLongExtra("currentTime", -10)
        remainTime = intent.getLongExtra("remainTime", -10)
        status = intent.getIntExtra("status", -10)
        stopwatch = TimerListItem(startTime, currentTime, remainTime, status)
        
    }
    
    private fun initViews() {
        stopwatch_stop_btn = findViewById<ImageButton>(R.id.stopwatch_activity_stop_btn)
        stopwatch_reset_btn = findViewById<ImageButton>(R.id.stopwatch_activity_reset_btn)
        stopwatch_start_btn = findViewById<ImageButton>(R.id.stopwatch_activity_start_btn)
        stopwatch_countdown = findViewById<CountdownView>(R.id.stopwatch_activity_countdown)
        
        stopwatch_start_btn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            when (stopwatch.status) {
                TimerListItem.ADDED -> {
                    stopwatch_countdown.start(stopwatch.startTime)
                    
                    stopwatch_stop_btn.visibility = View.VISIBLE
                    stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                }
                TimerListItem.FREEZED -> {
                    
                    stopwatch_countdown.start(stopwatch.currentTime)
                    
                    stopwatch_stop_btn.visibility = View.VISIBLE
                    stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                }
                TimerListItem.RUNNING -> {
                    stopwatch_countdown.start(stopwatch.startTime)
                    
                    stopwatch_stop_btn.visibility = View.VISIBLE
                    stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                }
                TimerListItem.RESETED -> {
                    stopwatch_countdown.start(stopwatch.startTime)
                    
                    stopwatch_stop_btn.visibility = View.VISIBLE
                    stopwatch_reset_btn.visibility = View.INVISIBLE
                    
                    stopwatch.status = TimerListItem.RUNNING
                    
                    
                }
                
            }
            db.updateOneItemInDatabase(DBHelper.STOPWATCH_TABLE, stopwatch, list_id)
            
            
        }
        
        stopwatch_stop_btn.setOnClickListener {
            stopwatch_countdown.stop()
            
            stopwatch.currentTime = stopwatch_countdown.remainTime
            
            stopwatch.status = TimerListItem.FREEZED
            stopwatch_stop_btn.visibility = View.INVISIBLE
            stopwatch_reset_btn.visibility = View.VISIBLE
            db.updateOneItemInDatabase(DBHelper.TIMER_TABLE, stopwatch, list_id)
            
            
        }
        
        stopwatch_reset_btn.setOnClickListener {
            stopwatch_countdown.stop()
            
            stopwatch.status = TimerListItem.RESETED
            stopwatch_stop_btn.visibility = View.VISIBLE
            stopwatch_reset_btn.visibility = View.INVISIBLE
            db.updateOneItemInDatabase(DBHelper.STOPWATCH_TABLE, stopwatch, list_id)
            
            resetItem(table, stopwatch, list_id)
            stopwatch_countdown.updateShow(stopwatch.startTime)
        }
        
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }
    
    private fun checkStatus() {
        
        when (stopwatch.status) {
            TimerListItem.ADDED -> {
                stopwatch_countdown.updateShow(stopwatch.startTime)
            }
            TimerListItem.FREEZED -> {
                stopwatch_countdown.stop()
                stopwatch_countdown.updateShow(stopwatch.currentTime)
                stopwatch_stop_btn.visibility = View.INVISIBLE
                stopwatch_reset_btn.visibility = View.VISIBLE
                
            }
            
            TimerListItem.RUNNING -> {
                stopwatch_countdown.start(stopwatch.remainTime - System.currentTimeMillis())
                stopwatch_stop_btn.visibility = View.VISIBLE
                stopwatch_reset_btn.visibility = View.INVISIBLE
                
            }
            TimerListItem.RESETED -> {
                stopwatch_countdown.stop()
                stopwatch_countdown.updateShow(stopwatch.startTime)
                
                stopwatch_stop_btn.visibility = View.VISIBLE
                stopwatch_reset_btn.visibility = View.INVISIBLE
                
            }
        }
        
    }
    
    private fun resetItem(table: String, timer: TimerListItem, position: Int) {
        timer.currentTime = timer.startTime
        timer.remainTime = timer.startTime
        timer.status = TimerListItem.RESETED
        
        val cv = ContentValues()
        cv.put(DBHelper.START_TIME_COL, timer.startTime)
        cv.put(DBHelper.CURRENT_TIME_COL, timer.startTime)
        cv.put(DBHelper.REMAIN_TIME_COl, timer.startTime)
        cv.put(DBHelper.IS_FREEZE_COl, timer.status)
        
        val id = db.getDatabaseItemIdByRecyclerViewItemId(table, position)
        wdb.update(DBHelper.TIMER_TABLE, cv, "id = ?", arrayOf(id.toString()))
        
        
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                //Needed for recreate the app after applying the settings
                // and to not reset timers. If use onRestart{recreate} it will reset timers.
                startActivityForResult(Intent(this, SettingsActivity::class.java), 1)
                
            }
            android.R.id.home -> { //Метод обработки нажатия на кнопку home.
                finish()
            }
        }
        
        return true
    }
    
    override fun onDestroy() {
        db.updateOneItemInDatabase(DBHelper.STOPWATCH_TABLE, stopwatch, list_id)
        super.onDestroy()
    }
    
    override fun onRestart() {
        super.onRestart()
        startActivity(Intent(this, StopWatchActivity::class.java))
        finish()
        overridePendingTransition(0, 0)
        
        
    }
}