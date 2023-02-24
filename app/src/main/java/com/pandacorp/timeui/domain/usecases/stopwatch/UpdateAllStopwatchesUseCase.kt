package com.pandacorp.timeui.domain.usecases.stopwatch

import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.domain.repositories.StopwatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAllStopwatchesUseCase @Inject constructor(private val stopwatchRepository: StopwatchRepository) {
    suspend operator fun invoke(items: MutableList<StopwatchItem>) = withContext(Dispatchers.IO) {
        stopwatchRepository.updateAll(items.toMutableList())
    }
}