package com.pandacorp.timeui.presentation.di.modules

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pandacorp.timeui.data.database.Database
import com.pandacorp.timeui.data.models.ClockDataItem
import com.pandacorp.timeui.data.models.StopwatchDataItem
import com.pandacorp.timeui.data.models.TimerDataItem
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.presentation.utils.Constants
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.TimeZone

@Module
class DependenciesModule {
    private lateinit var roomDatabase: Database

    @Provides
    fun provideContext(application: Application): Context = application

    @Provides
    fun provideDatabase(context: Context): Database {
        roomDatabase = Room.databaseBuilder(context, Database::class.java, Constants.Room.NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val startTime = (5 * 60 * 1000).toLong()
                        val timerItem =
                            TimerDataItem(
                                startTime = startTime,
                                currentTime = startTime,
                                status = TimerItem.ADDED
                            )

                        roomDatabase.clockDao().insert(ClockDataItem(timeZone = TimeZone.getDefault().id))
                        roomDatabase.stopwatchDao().insert(StopwatchDataItem())
                        roomDatabase.timerDao().insert(timerItem)
                    }
                }
            })
            .build()
        return roomDatabase
    }
}