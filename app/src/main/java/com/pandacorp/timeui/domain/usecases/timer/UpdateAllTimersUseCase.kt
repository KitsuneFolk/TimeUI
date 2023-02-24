package com.pandacorp.timeui.domain.usecases.timer

import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.domain.repositories.TimerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAllTimersUseCase @Inject constructor(private val timerRepository: TimerRepository) {
    suspend operator fun invoke(timerItems: MutableList<TimerItem>) = withContext(Dispatchers.IO) {
        timerRepository.updateAll(timerItems.toMutableList())
    }
}