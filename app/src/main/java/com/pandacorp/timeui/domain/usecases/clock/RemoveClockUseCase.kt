package com.pandacorp.timeui.domain.usecases.clock

import com.pandacorp.timeui.domain.models.ClockItem
import com.pandacorp.timeui.domain.repositories.ClockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveClockUseCase @Inject constructor(private val repository: ClockRepository) {
    suspend operator fun invoke(item: ClockItem) = withContext(Dispatchers.IO) {
        repository.removeItem(item)
    }
}