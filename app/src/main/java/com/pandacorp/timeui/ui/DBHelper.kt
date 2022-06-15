package com.pandacorp.timeui.ui

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TIMER_TABLE + " ("
                + ID_COL + " INTEGER PRIMARY KEY, "
                + REMAIN_TIME_COl + " TEXT,"
                + CURRENT_TIME_COL + " TEXT,"
                + IS_SHOW_STOP_COL + " INTEGER,"
                + IS_FREEZE_COl + " INTEGER" + ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TIMER_TABLE)
        onCreate(db)
    }
    fun getName(TABLE_NAME: String): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

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

        // below is the variable for name column
        val TIME_COl = "time"

        val REMAIN_TIME_COl = "remain_time"

        val CURRENT_TIME_COL = "current_time"

        // below is the variable for freezing the time if stop btn is clicked after user
        // went in the app
        val IS_SHOW_STOP_COL = "is_show_stop"

        val IS_FREEZE_COl = "is_freeze"

    }
}