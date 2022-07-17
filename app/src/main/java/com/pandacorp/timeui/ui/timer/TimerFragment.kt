package com.pandacorp.timeui.ui.timer

import android.app.AlertDialog
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
import com.pandacorp.timeui.adapter.TimerCustomAdapter
import com.pandacorp.timeui.adapter.TimerListItem
import com.pandacorp.timeui.adapter.TimerRecyclerItemTouchHelper
import com.pandacorp.timeui.settings.MySettings
import com.pandacorp.timeui.ui.DBHelper
import kotlinx.android.synthetic.main.timer_list_item.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.defaultSharedPreferences


class TimerFragment : Fragment(), TimerRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private val TAG = "MyLogs"

    private lateinit var root: View

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var dialog_timePicker: TimePicker

    private lateinit var dialog_button_accept: Button
    private lateinit var dialog_button_close: ImageButton

    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase
    private lateinit var cursor: Cursor

    private var timers = arrayListOf<TimerListItem>()

    private lateinit var customAdapter: TimerCustomAdapter

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_timer, container, false)


        CoroutineScope(Dispatchers.Main).launch {
            initViews()

            checkIsFirstTime()
        }



        return root


    }

    private suspend fun initViews() {
        setRecyclerView()


        fab = root.findViewById(R.id.timer_add_fab)
        fab.setOnClickListener { setDialog() }


    }

    private fun checkIsFirstTime() {
        //If app opened first time then add one test timer for user.
        val sp = requireActivity().defaultSharedPreferences
        val edit = sp.edit()
        if (sp.getBoolean("isFirstTime", true)) {
            val startTime = (5 * 60 * 1000).toLong()
            val timerListItem =
                TimerListItem(startTime, startTime, startTime, TimerListItem.ADDED)
            timers.add(timerListItem)
            customAdapter.notifyItemInserted(timers.size)
            db.add(timerListItem)
            edit.putBoolean("isFirstTime", false)
            edit.apply()
        }
    }

    private fun setDialog() {
        val view = layoutInflater.inflate(R.layout.timer_time_picker, null)

        val dialog = AlertDialog.Builder(context).create()
        dialog.window!!.setBackgroundDrawableResource(MySettings.getBackgroundColor(requireContext()))

        dialog.setView(view)

        dialog_timePicker = view.findViewById(R.id.dialog_timePicker)
        dialog_timePicker.setIs24HourView(true)
        dialog_timePicker.setCurrentSecond(0)
        dialog_timePicker.currentMinute = 5
        dialog_timePicker.currentHour = 0

        dialog_button_accept = view.findViewById(R.id.dialog_button_accept)
        dialog_button_accept.setOnClickListener {
            val startTime = timeToTimeInMillis(
                dialog_timePicker.currentHour,
                dialog_timePicker.currentMinute,
                dialog_timePicker.currentSeconds
            )
            val currentTime = startTime
            val remainTime = currentTime
            val isFreeze = TimerListItem.ADDED

            val timerListItem = TimerListItem(startTime, currentTime, remainTime, isFreeze)
            timers.add(timerListItem)
            customAdapter.notifyItemInserted(timers.size)
            db.add(timerListItem)
            //Close the dialog. Without this expression dialog won't close
            // when accept btn is clicked
            dialog.dismiss()
        }
        dialog_button_close = view.findViewById(R.id.dialog_button_close)
        dialog_button_close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

    private fun timeToTimeInMillis(hours: Int, minutes: Int, seconds: Int): Long {
        val timeInMillis = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000)
        return timeInMillis.toLong()
    }

    private suspend fun setRecyclerView() = withContext(Dispatchers.Main) {
        getDatabaseTimers()
        customAdapter = TimerCustomAdapter(timers)

        recyclerView = root.findViewById(R.id.timer_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = customAdapter

        enableSwipe()
        registerForContextMenu(recyclerView)


    }

    private fun getDatabaseTimers() {
        //Creating DBHelper object
        db = DBHelper(requireContext(), null)

        //Creating WritableDatabase object
        wdb = db.writableDatabase

        //Creating Cursor object
        cursor = db.getCursor(DBHelper.TIMER_TABLE)!!

        //Uploading the timers when opening the app
        val START_TIME_COL = cursor.getColumnIndex(DBHelper.START_TIME_COL)
        val CURRENT_TIME_COL = cursor.getColumnIndex(DBHelper.CURRENT_TIME_COL)
        val REMAIN_TIME_COL = cursor.getColumnIndex(DBHelper.REMAIN_TIME_COl)
        val IS_FREEZE_COL = cursor.getColumnIndex(DBHelper.IS_FREEZE_COl)
        if (cursor.moveToFirst()) {
            do {
                val timer = TimerListItem(
                    cursor.getLong(START_TIME_COL),
                    cursor.getLong(CURRENT_TIME_COL),
                    cursor.getLong(REMAIN_TIME_COL),
                    cursor.getInt(IS_FREEZE_COL)

                )

                timers.add(timer)


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
            TimerRecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is TimerCustomAdapter.ViewHolder) {
            customAdapter.removeItem(position)
            // deleting database item
            db.removeById(position)

        }
    }


    override fun onDestroy() {
        val db = DBHelper(requireContext(), null)

        for ((index, timer) in timers.withIndex()) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(index)
            if (viewHolder != null) {
                timer.currentTime =
                    viewHolder.itemView.timer_countdown.remainTime
                timer.remainTime = System.currentTimeMillis() + timer.currentTime
                timer.isFreeze = timer.isFreeze
            }


        }
        db.updateAllTimersInDatabase(timers)


        super.onDestroy()

    }

}