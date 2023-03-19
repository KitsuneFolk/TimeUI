package com.pandacorp.timeui.presentation.di.app

import android.app.Application
import com.pandacorp.timeui.presentation.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidInjectionModule::class,
            InjectorModule::class,
            ClockModule::class,
            TimerModule::class,
            StopwatchModule::class,
            ViewModelModule::class
        ])
interface AppComponent : AndroidInjector<App> {
    
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        
        fun build(): AppComponent
    }
    
}

