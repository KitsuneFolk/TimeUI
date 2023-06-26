package com.pandacorp.timeui.presentation.di.modules

import com.pandacorp.timeui.data.database.Database
import com.pandacorp.timeui.data.database.TimerDao
import com.pandacorp.timeui.data.mappers.TimerMapper
import com.pandacorp.timeui.data.repositories.TimerRepositoryImpl
import com.pandacorp.timeui.domain.repositories.TimerRepository
import com.pandacorp.timeui.domain.usecases.timer.AddTimerUseCase
import com.pandacorp.timeui.domain.usecases.timer.GetTimersUseCase
import com.pandacorp.timeui.domain.usecases.timer.RemoveAllTimersUseCase
import com.pandacorp.timeui.domain.usecases.timer.RemoveTimerUseCase
import com.pandacorp.timeui.domain.usecases.timer.UpdateAllTimersUseCase
import com.pandacorp.timeui.domain.usecases.timer.UpdateTimerUseCase
import com.pandacorp.timeui.presentation.vm.TimerViewModel
import dagger.Module
import dagger.Provides

@Module
class TimerModule {
    @Provides
    fun provideTimerMapper(): TimerMapper = TimerMapper()
    
    @Provides
    fun provideTimerDao(database: Database): TimerDao = database.timerDao()
    
    @Provides
    fun provideTimerRepositoryImpl(timerDao: TimerDao, timerMapper: TimerMapper): TimerRepository =
        TimerRepositoryImpl(timerDao, timerMapper)
    
    @Provides
    fun provideAddTimerUseCase(timerRepositoryImpl: TimerRepository): AddTimerUseCase =
        AddTimerUseCase(timerRepositoryImpl)
    
    @Provides
    fun provideGetAllTimersUseCase(timerRepositoryImpl: TimerRepository): GetTimersUseCase =
        GetTimersUseCase(timerRepositoryImpl)
    
    @Provides
    fun provideRemoveTimerUseCase(timerRepositoryImpl: TimerRepository): RemoveTimerUseCase =
        RemoveTimerUseCase(timerRepositoryImpl)
    
    @Provides
    fun provideRemoveAllTimersUseCase(timerRepositoryImpl: TimerRepository): RemoveAllTimersUseCase =
        RemoveAllTimersUseCase(timerRepositoryImpl)
    
    @Provides
    fun provideUpdateTimerUseCase(timerRepositoryImpl: TimerRepository): UpdateTimerUseCase =
        UpdateTimerUseCase(timerRepositoryImpl)
    
    @Provides
    fun provideUpdateAllTimersUseCase(timerRepositoryImpl: TimerRepository): UpdateAllTimersUseCase =
        UpdateAllTimersUseCase(timerRepositoryImpl)
    
    @Provides
    fun provideTimerViewModel(
        getTimersUseCase: GetTimersUseCase,
        addTimerUseCase: AddTimerUseCase,
        removeTimerUseCase: RemoveTimerUseCase,
        removeAllTimersUseCase: RemoveAllTimersUseCase,
        updateTimerUseCase: UpdateTimerUseCase,
        updateAllTimersUseCase: UpdateAllTimersUseCase
    ): TimerViewModel = TimerViewModel(
            getTimersUseCase,
            addTimerUseCase,
            removeTimerUseCase,
            removeAllTimersUseCase,
            updateTimerUseCase,
            updateAllTimersUseCase)
}