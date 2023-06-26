package com.pandacorp.timeui.presentation.di.modules

import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.ui.fragments.ClockFragment
import com.pandacorp.timeui.presentation.ui.fragments.StopwatchFragment
import com.pandacorp.timeui.presentation.ui.fragments.TimerFragment
import com.pandacorp.timeui.presentation.ui.screen.StopWatchScreen
import com.pandacorp.timeui.presentation.ui.screen.TimeZoneScreen
import com.pandacorp.timeui.presentation.ui.screen.TimerScreen
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class InjectorModule {
    @ContributesAndroidInjector
    abstract fun bindClockFragment(): ClockFragment

    @ContributesAndroidInjector
    abstract fun bindStopwatchFragment(): StopwatchFragment

    @ContributesAndroidInjector
    abstract fun bindTimerFragment(): TimerFragment

    @ContributesAndroidInjector
    abstract fun bindTimeZoneScreen(): TimeZoneScreen

    @ContributesAndroidInjector
    abstract fun bindStopwatchScreen(): StopWatchScreen

    @ContributesAndroidInjector
    abstract fun bindTimerScreen(): TimerScreen

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

}