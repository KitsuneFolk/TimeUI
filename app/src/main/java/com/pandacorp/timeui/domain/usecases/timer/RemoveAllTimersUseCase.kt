package com.pandacorp.timeui.domain.usecases.timer

import com.pandacorp.timeui.domain.repositories.TimerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveAllTimersUseCase @Inject constructor(private val timerRepository: TimerRepository) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        timerRepository.removeAll()
    }
}