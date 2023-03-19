package com.pandacorp.timeui.presentation.di.modules

import com.pandacorp.timeui.data.database.ClockDao
import com.pandacorp.timeui.data.database.Database
import com.pandacorp.timeui.data.mappers.ClockMapper
import com.pandacorp.timeui.data.repositories.ClockRepositoryImpl
import com.pandacorp.timeui.domain.usecases.clock.AddClockUseCase
import com.pandacorp.timeui.domain.usecases.clock.GetClocksUseCase
import com.pandacorp.timeui.domain.usecases.clock.RemoveAllClocksUseCase
import com.pandacorp.timeui.domain.usecases.clock.RemoveClockUseCase
import com.pandacorp.timeui.presentation.vm.ClockViewModel
import dagger.Module
import dagger.Provides

@Module
class ClockModule {
    @Provides
    fun provideClockDao(database: Database): ClockDao = database.clockDao()
    
    @Provides
    fun provideClockMapper(): ClockMapper = ClockMapper()
    
    @Provides
    fun provideClockRepositoryImpl(
        ClockDao: ClockDao,
        ClockMapper: ClockMapper
    ) = ClockRepositoryImpl(ClockDao, ClockMapper)
    
    @Provides
    fun provideAddClockUseCase(ClockRepositoryImpl: ClockRepositoryImpl): AddClockUseCase =
        AddClockUseCase(ClockRepositoryImpl)
    
    @Provides
    fun provideGetAllClocksUseCase(ClockRepositoryImpl: ClockRepositoryImpl): GetClocksUseCase =
        GetClocksUseCase(ClockRepositoryImpl)
    
    @Provides
    fun provideRemoveClockUseCase(ClockRepositoryImpl: ClockRepositoryImpl): RemoveClockUseCase =
        RemoveClockUseCase(ClockRepositoryImpl)
    
    @Provides
    fun provideRemoveAllClocksUseCase(ClockRepositoryImpl: ClockRepositoryImpl): RemoveAllClocksUseCase =
        RemoveAllClocksUseCase(ClockRepositoryImpl)
    
    @Provides
    fun provideClockViewModel(
        getClocksUseCase: GetClocksUseCase,
        addClocksUseCase: AddClockUseCase,
        removeClockUseCase: RemoveClockUseCase,
        removeAllClocksUseCase: RemoveAllClocksUseCase
    ): ClockViewModel = ClockViewModel(
            getClocksUseCase,
            addClocksUseCase,
            removeClockUseCase,
            removeAllClocksUseCase)
}