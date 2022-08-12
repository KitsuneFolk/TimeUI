package com.pandacorp.timeui.ui.timer

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

class TimerActivity : AppCompatActivity() {
    private val table = DBHelper.TIMER_TABLE
    
    private lateinit var timer_stop_btn: ImageButton
    private lateinit var timer_reset_btn: ImageButton
    private lateinit var timer_start_btn: ImageButton
    private lateinit var timer_countdown: CountdownView
    
    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase
    
    private var list_id = -10
    private var startTime = -10L
    private var currentTime = -10L
    private var remainTime = -10L
    private var status = -10

    private lateinit var timer: TimerListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        val mySettings = MySettings(this)
        mySettings.start()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

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
        timer = TimerListItem(startTime, currentTime, remainTime, status)

    }

    private fun initViews() {
        timer_stop_btn = findViewById<ImageButton>(R.id.timer_activity_stop_btn)
        timer_reset_btn = findViewById<ImageButton>(R.id.timer_activity_reset_btn)
        timer_start_btn = findViewById<ImageButton>(R.id.timer_activity_start_btn)
        timer_countdown = findViewById<CountdownView>(R.id.timer_activity_countdown)

        timer_start_btn.setOnClickListener {
            // if the stop btn is pressed, then when the start btn
            //  is pressed, start counting from currentTime,
            // if the reset btn is pressed then when start btn
            //  is pressed, start counting from startTime
            when (timer.status) {
                TimerListItem.ADDED -> {
                    timer_countdown.start(timer.startTime)

                    timer_stop_btn.visibility = View.VISIBLE
                    timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING
                }
                TimerListItem.FREEZED -> {

                    timer_countdown.start(timer.currentTime)

                    timer_stop_btn.visibility = View.VISIBLE
                    timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING
                }
                TimerListItem.RUNNING -> {
                    timer_countdown.start(timer.startTime)

                    timer_stop_btn.visibility = View.VISIBLE
                    timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING
                }
                TimerListItem.RESETED -> {
                    timer_countdown.start(timer.startTime)

                    timer_stop_btn.visibility = View.VISIBLE
                    timer_reset_btn.visibility = View.INVISIBLE

                    timer.status = TimerListItem.RUNNING


                }

            }
            db.updateOneItemInDatabase(DBHelper.TIMER_TABLE, timer, list_id)


        }

        timer_stop_btn.setOnClickListener {
            timer_countdown.stop()
    
            timer.currentTime = timer_countdown.remainTime
    
            timer.status = TimerListItem.FREEZED
            timer_stop_btn.visibility = View.INVISIBLE
            timer_reset_btn.visibility = View.VISIBLE
            db.updateOneItemInDatabase(DBHelper.TIMER_TABLE, timer, list_id)
    
    
        }

        timer_reset_btn.setOnClickListener {
            timer_countdown.stop()
    
            timer.status = TimerListItem.RESETED
            timer_stop_btn.visibility = View.VISIBLE
            timer_reset_btn.visibility = View.INVISIBLE
            db.updateOneItemInDatabase(DBHelper.TIMER_TABLE, timer, list_id)
    
            resetItem(timer, list_id)
            timer_countdown.updateShow(timer.startTime)
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    private fun resetItem(timer: TimerListItem, position: Int) {
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

    private fun checkStatus() {

        when (timer.status) {
            TimerListItem.ADDED -> {
                timer_countdown.updateShow(timer.startTime)
            }
            TimerListItem.FREEZED -> {
                timer_countdown.stop()
                timer_countdown.updateShow(timer.currentTime)
                timer_stop_btn.visibility = View.INVISIBLE
                timer_reset_btn.visibility = View.VISIBLE

            }

            TimerListItem.RUNNING -> {
                timer_countdown.start(timer.remainTime - System.currentTimeMillis())
                timer_stop_btn.visibility = View.VISIBLE
                timer_reset_btn.visibility = View.INVISIBLE

            }
            TimerListItem.RESETED -> {
                timer_countdown.stop()
                timer_countdown.updateShow(timer.startTime)

                timer_stop_btn.visibility = View.VISIBLE
                timer_reset_btn.visibility = View.INVISIBLE

            }
        }

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
        db.updateOneItemInDatabase(DBHelper.TIMER_TABLE, timer, list_id)
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        startActivity(Intent(this, TimerActivity::class.java))
        finish()
        overridePendingTransition(0, 0)


    }

}