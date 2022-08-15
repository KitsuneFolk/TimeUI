package com.pandacorp.timeui.ui.stopwatch

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ikovac.timepickerwithseconds.TimePicker
import com.pandacorp.timeui.R
import com.pandacorp.timeui.ui.DBHelper
import com.pandacorp.timeui.ui.stopwatch.adapter.StopWatchCustomAdapter
import com.pandacorp.timeui.ui.stopwatch.adapter.StopWatchRecyclerItemTouchHelper
import com.pandacorp.timeui.ui.timer.adapter.TimerListItem
import kotlinx.android.synthetic.main.stopwatch_list_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.defaultSharedPreferences
import java.text.SimpleDateFormat


class StopWatchFragment : Fragment(),
    StopWatchRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private val TAG = "MyLogs"
    private val table = DBHelper.STOPWATCH_TABLE
    
    private lateinit var root: View
    
    private var stopwatches = arrayListOf<TimerListItem>()
    
    private lateinit var customAdapter: StopWatchCustomAdapter
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var dialog_timePicker: TimePicker
    
    private lateinit var dialog_button_accept: Button
    private lateinit var dialog_button_close: ImageButton
    
    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase
    private lateinit var cursor: Cursor
    
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_stopwatch, container, false)
        
        CoroutineScope(Dispatchers.Main).launch {
            initViews()
            checkIsFirstTime()
            
        }
        
        return root
        
    }
    
    private suspend fun initViews() {
        
        setRecyclerView()
        
        fab = root.findViewById(R.id.stopwatch_add_fab)
        fab.setOnClickListener { setDialog() }
        
    }
    
    private fun checkIsFirstTime() {
        //If app opened first time then add one test timer for user.
        val sp = requireActivity().defaultSharedPreferences
        val edit = sp.edit()
        if (sp.getBoolean("isStopWatchFirstTime", true)) {
            val timerListItem =
                TimerListItem(0, 0, 0, TimerListItem.ADDED)
            stopwatches.add(timerListItem)
            customAdapter.notifyItemInserted(stopwatches.size)
            db.add(DBHelper.STOPWATCH_TABLE, timerListItem)
            edit.putBoolean("isStopWatchFirstTime", false)
            edit.apply()
        }
    }
    
    private fun setDialog() {
        val time = 0L
        val startTime = time
        val currentTime = time
        val remainTime = time
        val status = TimerListItem.ADDED
        
        val timerListItem = TimerListItem(startTime, currentTime, remainTime, status)
        stopwatches.add(timerListItem)
        customAdapter.notifyItemInserted(stopwatches.size)
        db.add(table, timerListItem)
        //Close the dialog. Without this expression dialog won't close
        // when accept btn is clicked
        
        
    }
    
    private suspend fun setRecyclerView() = withContext(Dispatchers.Main) {
        getDatabaseTimers()
        customAdapter =
            StopWatchCustomAdapter(this@StopWatchFragment.requireActivity(), stopwatches)
        
        recyclerView = root.findViewById(R.id.stopwatch_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = customAdapter
        
        enableSwipe()
        registerForContextMenu(recyclerView)
        
        
    }
    
    private fun getDatabaseTimers() {//Creating DBHelper object
        /* val time = 0L
         stopwatches.add(TimerListItem(time, time, time, TimerListItem.ADDED))
         */
        //Creating DBHelper object
        db = DBHelper(requireContext(), null)
        
        //Creating WritableDatabase object
        wdb = db.writableDatabase
        
        //Creating Cursor object
        cursor = db.getCursor(DBHelper.STOPWATCH_TABLE)!!
        
        //Uploading the timers when opening the app
        val START_TIME_COL = cursor.getColumnIndex(DBHelper.START_TIME_COL)
        val CURRENT_TIME_COL = cursor.getColumnIndex(DBHelper.CURRENT_TIME_COL)
        val REMAIN_TIME_COL = cursor.getColumnIndex(DBHelper.REMAIN_TIME_COl)
        val IS_FREEZE_COL = cursor.getColumnIndex(DBHelper.IS_FREEZE_COl)
        if (cursor.moveToFirst()) {
            do {
                val stopwatch = TimerListItem(
                        cursor.getLong(START_TIME_COL),
                        cursor.getLong(CURRENT_TIME_COL),
                        cursor.getLong(REMAIN_TIME_COL),
                        cursor.getInt(IS_FREEZE_COL)
                
                )
                
                stopwatches.add(stopwatch)
                
                
            } while (cursor.moveToNext())
        }
        
    }
    
    private fun enableSwipe() {
        //Attached the ItemTouchHelper
        recyclerView.addItemDecoration(
                DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.VERTICAL
                )
        )
        
        //Attached the ItemTouchHelper
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            StopWatchRecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }
    
    
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is StopWatchCustomAdapter.ViewHolder) {
            customAdapter.removeItem(position)
            // deleting database item
            db.removeById(table, position)
            
        }
    }
    
    override fun onDestroy() {
        val db = DBHelper(requireContext(), null)
        
        for ((index, stopwatch) in stopwatches.withIndex()) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(index)
            if (viewHolder != null) {
                val time = SimpleDateFormat("HH:mm:ss").parse(
                        viewHolder.itemView.stopwatch_textview.text as String
                )
                
                
                val seconds = (time.hours * 3600) + (time.minutes * 60) + (time.seconds)
                stopwatch.currentTime = (seconds * 1000).toLong()
                
                stopwatch.remainTime = System.currentTimeMillis() + stopwatch.currentTime
                stopwatch.status = stopwatch.status
    
    
            }
    
    
        }
        db.updateAllTimersInDatabase(DBHelper.STOPWATCH_TABLE, stopwatches)
    
        val sp = requireContext().defaultSharedPreferences
        val edit = sp.edit()
        edit.putLong("stopwatch_currentTimeMillis", System.currentTimeMillis())
        edit.apply()
    
    
        super.onDestroy()
    
    }
    
    
}