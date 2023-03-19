package com.pandacorp.timeui.presentation.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pandacorp.timeui.presentation.vm.ClockViewModel
import com.pandacorp.timeui.presentation.vm.StopwatchViewModel
import com.pandacorp.timeui.presentation.vm.TimerViewModel
import com.pandacorp.timeui.presentation.vm.ViewModelFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton
import kotlin.reflect.KClass

@Suppress("unused")
@Module
abstract class ViewModelModule {
    
    @Binds
    @IntoMap
    @ViewModelKey(ClockViewModel::class)
    abstract fun bindClockViewModel(clockViewModel: ClockViewModel): ViewModel
    
    @Binds
    @IntoMap
    @ViewModelKey(TimerViewModel::class)
    abstract fun bindTimerViewModel(timerViewModel: TimerViewModel): ViewModel
    
    @Singleton
    @Binds
    @IntoMap
    @ViewModelKey(StopwatchViewModel::class)
    abstract fun bindStopwatchViewModel(stopwatchViewModel: StopwatchViewModel): ViewModel
    
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)