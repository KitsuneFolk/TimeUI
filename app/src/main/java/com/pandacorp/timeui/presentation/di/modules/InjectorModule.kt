package com.pandacorp.timeui.presentation.di.modules

import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.ui.stopwatch.StopWatchFragment
import com.pandacorp.timeui.presentation.ui.timer.TimerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class InjectorModule {
    @ContributesAndroidInjector
    abstract fun bindTimerFragment(): TimerFragment
    
    @ContributesAndroidInjector
    abstract fun bindStopwatchFragment(): StopWatchFragment
    
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
    
}