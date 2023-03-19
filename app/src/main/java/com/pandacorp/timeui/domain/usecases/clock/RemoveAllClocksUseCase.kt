package com.pandacorp.timeui.domain.usecases.clock

import com.pandacorp.timeui.domain.repositories.ClockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveAllClocksUseCase @Inject constructor(private val repository: ClockRepository) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repository.removeAll()
    }
}