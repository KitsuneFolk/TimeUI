package com.pandacorp.timeui.domain.usecases.stopwatch

import com.pandacorp.timeui.domain.repositories.StopwatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetStopwatchesUseCase @Inject constructor(private val stopwatchRepository: StopwatchRepository) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        return@withContext stopwatchRepository.getAll()
    }
}