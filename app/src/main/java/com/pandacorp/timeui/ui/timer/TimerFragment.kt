package com.pandacorp.timeui.ui.timer

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pandacorp.timeui.R
import com.pandacorp.timeui.adapter.TimerCustomAdapter
import com.pandacorp.timeui.adapter.TimerListItem
import com.pandacorp.timeui.adapter.TimerRecyclerItemTouchHelper
import com.pandacorp.timeui.ui.DBHelper
import kotlinx.android.synthetic.main.timer_list_item.view.*

class TimerFragment : Fragment(), TimerRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private val TAG = "MyLogs"

    private lateinit var root: View

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase
    private lateinit var cursor: Cursor

    private var timers = arrayListOf<TimerListItem>()

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
        setRecyclerView()

        fab = root.findViewById(R.id.timer_add_fab)
        fab.setOnClickListener {
            val currentTime = (1000 * 60 * 60 * 3).toLong()
            val remainTime = currentTime
            val isFreeze = false

            val timerListItem = TimerListItem(currentTime, remainTime, isFreeze)
            timers.add(timerListItem)
            customAdapter.notifyItemInserted(timers.size)
            db.add(timerListItem)


        }


    }

    private fun setRecyclerView() {
        //Creating DBHelper object
        db = DBHelper(requireContext(), null)

        //Creating WritableDatabase object
        wdb = db.writableDatabase

        //Creating Cursor object
        cursor = db.getCursor(DBHelper.TIMER_TABLE)!!

        //Uploading the timers when opening the app
        val CURRENT_TIME_COL = cursor.getColumnIndex(DBHelper.CURRENT_TIME_COL)
        val REMAIN_TIME_COL = cursor.getColumnIndex(DBHelper.REMAIN_TIME_COl)
        val IS_FREEZE_COL = cursor.getColumnIndex(DBHelper.IS_FREEZE_COl)
        if (cursor.moveToFirst()) {
            do {
                val timer = TimerListItem(
                    cursor.getLong(CURRENT_TIME_COL),
                    cursor.getLong(REMAIN_TIME_COL),
                    when (cursor.getInt(IS_FREEZE_COL)) {
                        0 -> true
                        1 -> false
                        else -> throw Exception(
                            "Value can be only 0 or 1, value = ${
                                cursor.getInt(
                                    IS_FREEZE_COL
                                )
                            }"
                        )
                    }

                )

                timers.add(timer)
                Log.d(
                    TAG,
                    "setRecyclerView: value can only be 0 or 1, value = ${
                        cursor.getInt(IS_FREEZE_COL)
                    }"
                )

            } while (cursor.moveToNext())
        }
        Log.d(TAG, "setRecyclerView: timers = $timers")
        //-826162799

        customAdapter = TimerCustomAdapter(timers)

        recyclerView = root.findViewById(R.id.timer_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = customAdapter

        enableSwipe()
        registerForContextMenu(recyclerView)


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
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(index)!!
            timer.currentTime =
                viewHolder.itemView.timer_countdown.remainTime
            timer.remainTime = System.currentTimeMillis() + timer.currentTime
            timer.isFreeze = timer.isFreeze
            Log.d(
                TAG,
                "onDestroy: timers[$index] = ${timers[index].currentTime}, ${timers[index].remainTime}, ${timers[index].isFreeze}"
            )


        }
        db.updateAllTimersInDatabase(timers)


        super.onDestroy()

    }
}