package com.pandacorp.timeui.ui

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.pandacorp.timeui.adapter.TimerListItem

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    private val TAG = "MyLogs"

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TIMER_TABLE + " ("
                + ID_COL + " INTEGER PRIMARY KEY, "
                + START_TIME_COL + " INTEGER,"
                + REMAIN_TIME_COl + " INTEGER,"
                + CURRENT_TIME_COL + " INTEGER,"
                + IS_FREEZE_COl + " INTEGER" + ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TIMER_TABLE)
        onCreate(db)
    }

    fun getCursor(TABLE_NAME: String): Cursor? {
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

    }

    fun add(timerListItem: TimerListItem) {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(START_TIME_COL, timerListItem.startTime)
        cv.put(CURRENT_TIME_COL, timerListItem.currentTime)
        Log.d(TAG, "add: currentTime = ${timerListItem.currentTime}")
        cv.put(REMAIN_TIME_COl, timerListItem.remainTime)
        cv.put(IS_FREEZE_COl, (timerListItem.isFreeze))

        db.insert(TIMER_TABLE, null, cv)


    }

    fun removeById(id: Int) {

        val db = this.writableDatabase
        val deletedPosition = getDatabaseItemIdByRecyclerViewItemId(id)

        db.delete(DBHelper.TIMER_TABLE, DBHelper.ID_COL + "=?", arrayOf("$deletedPosition"))


    }

    fun getDatabaseItemIdByRecyclerViewItemId(id: Int): Int? {
        val cursor = getCursor(DBHelper.TIMER_TABLE)
        //var of Id number in Timer_Table to understand how much there elements is
        var numberOfIds = 0
        //var of position of the deleted item
        var deletedPosition: Int?

        if (cursor!!.moveToFirst()) {
            val ID_COL = cursor.getColumnIndex(DBHelper.ID_COL)
            do {
                numberOfIds++
                if (numberOfIds == id + 1) {

                    deletedPosition = cursor.getInt(ID_COL)
                    if (deletedPosition == null) {
                        throw Exception("deletedPosition cannot be null!")
                    }
                    return deletedPosition
                }
            } while (cursor.moveToNext())
        }
        return null


    }

    fun updateAllTimersInDatabase(timers: ArrayList<TimerListItem>) {
        val db = writableDatabase

        timers.forEach { timer ->
            val cv = ContentValues()
            cv.put(CURRENT_TIME_COL, timer.currentTime)
            cv.put(REMAIN_TIME_COl, timer.remainTime)
            cv.put(IS_FREEZE_COl, timer.isFreeze)
            val id = getDatabaseItemIdByRecyclerViewItemId(timers.indexOf(timer))
            db.update(TIMER_TABLE, cv, "id = ?", arrayOf(id.toString()))
        }
    }

    fun updateOneTimerInDatabase(timer: TimerListItem, position: Int) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(CURRENT_TIME_COL, timer.currentTime)
        cv.put(REMAIN_TIME_COl, timer.remainTime)
        cv.put(IS_FREEZE_COl, timer.isFreeze)
        val id = getDatabaseItemIdByRecyclerViewItemId(position)
        db.update(TIMER_TABLE, cv, "id = ?", arrayOf(id.toString()))


    }


    companion object {
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "TimeUI"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TIMER_TABLE = "timer_table"

        // below is the variable for id column
        val ID_COL = "id"

        // variable needed for time of starting the timer
        val START_TIME_COL = "START_TIME_COL"

        val REMAIN_TIME_COl = "remain_time"

        val CURRENT_TIME_COL = "current_time"

        // below is the variable for freezing the time if stop btn is clicked after user
        // went in the app
        val IS_FREEZE_COl = "is_freeze"

    }
}