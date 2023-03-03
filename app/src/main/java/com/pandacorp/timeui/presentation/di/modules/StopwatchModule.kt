package com.pandacorp.timeui.presentation.di.modules

import com.pandacorp.timeui.data.database.Database
import com.pandacorp.timeui.data.database.StopwatchDao
import com.pandacorp.timeui.data.mappers.StopwatchMapper
import com.pandacorp.timeui.data.repositories.StopwatchRepositoryImpl
import com.pandacorp.timeui.domain.usecases.stopwatch.*
import com.pandacorp.timeui.presentation.vm.StopwatchViewModel
import dagger.Module
import dagger.Provides

@Module
class StopwatchModule {
    @Provides
    fun provideStopwatchDao(database: Database): StopwatchDao = database.stopwatchDao()
    
    @Provides
    fun provideStopwatchMapper(): StopwatchMapper = StopwatchMapper()
    
    @Provides
    fun provideStopwatchRepositoryImpl(
        stopwatchDao: StopwatchDao,
        stopwatchMapper: StopwatchMapper
    ) = StopwatchRepositoryImpl(stopwatchDao, stopwatchMapper)
    
    @Provides
    fun provideAddStopwatchUseCase(stopwatchRepositoryImpl: StopwatchRepositoryImpl): AddStopwatchUseCase =
        AddStopwatchUseCase(stopwatchRepositoryImpl)
    
    @Provides
    fun provideGetAllStopwatchesUseCase(stopwatchRepositoryImpl: StopwatchRepositoryImpl): GetStopwatchesUseCase =
        GetStopwatchesUseCase(stopwatchRepositoryImpl)
    
    @Provides
    fun provideRemoveStopwatchUseCase(stopwatchRepositoryImpl: StopwatchRepositoryImpl): RemoveStopwatchUseCase =
        RemoveStopwatchUseCase(stopwatchRepositoryImpl)
    
    @Provides
    fun provideRemoveAllStopwatchesUseCase(stopwatchRepositoryImpl: StopwatchRepositoryImpl): RemoveAllStopwatchesUseCase =
        RemoveAllStopwatchesUseCase(stopwatchRepositoryImpl)
    
    @Provides
    fun provideUpdateStopwatchUseCase(stopwatchRepositoryImpl: StopwatchRepositoryImpl): UpdateStopwatchUseCase =
        UpdateStopwatchUseCase(stopwatchRepositoryImpl)
    
    @Provides
    fun provideUpdateAllStopwatchesUseCase(stopwatchRepositoryImpl: StopwatchRepositoryImpl): UpdateAllStopwatchesUseCase =
        UpdateAllStopwatchesUseCase(stopwatchRepositoryImpl)
    
    @Provides
    fun provideStopwatchViewModel(
        getStopwatchesUseCase: GetStopwatchesUseCase,
        addStopwatchesUseCase: AddStopwatchUseCase,
        removeStopwatchUseCase: RemoveStopwatchUseCase,
        removeAllStopwatchesUseCase: RemoveAllStopwatchesUseCase,
        updateStopwatchUseCase: UpdateStopwatchUseCase,
        updateAllStopwatchesUseCase: UpdateAllStopwatchesUseCase
    ): StopwatchViewModel = StopwatchViewModel(
            getStopwatchesUseCase,
            addStopwatchesUseCase,
            removeStopwatchUseCase,
            removeAllStopwatchesUseCase,
            updateStopwatchUseCase,
            updateAllStopwatchesUseCase)
}