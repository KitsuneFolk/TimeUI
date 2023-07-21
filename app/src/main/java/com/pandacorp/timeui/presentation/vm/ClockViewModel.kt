package com.pandacorp.timeui.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.timeui.domain.models.ClockItem
import com.pandacorp.timeui.domain.usecases.clock.AddClockUseCase
import com.pandacorp.timeui.domain.usecases.clock.GetClocksUseCase
import com.pandacorp.timeui.domain.usecases.clock.RemoveAllClocksUseCase
import com.pandacorp.timeui.domain.usecases.clock.RemoveClockUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClockViewModel @Inject constructor(
    private val getAllUseCase: GetClocksUseCase,
    private val addUseCase: AddClockUseCase,
    private val removeUseCase: RemoveClockUseCase,
    private val removeAllUseCase: RemoveAllClocksUseCase,
) :
    ViewModel() {
    private val _clocksList = MutableLiveData<MutableList<ClockItem>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getAllUseCase().apply {
                    postValue(this)
                }
            }
        }
    }
    val clocksList: LiveData<MutableList<ClockItem>> = _clocksList

    fun addItem(clockItem: ClockItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = addUseCase(clockItem)
            clockItem.id = id
            _clocksList.apply {
                value?.add(clockItem)
                postValue(value)
            }
        }
    }

    private fun removeItem(clockItem: ClockItem) {
        _clocksList.apply {
            value?.remove(clockItem)
            postValue(value)
        }
        CoroutineScope(Dispatchers.IO).launch { removeUseCase(clockItem) }
    }

    fun removeAt(position: Int) {
        removeItem(_clocksList.value?.get(position) ?: return)
    }


    fun removeAll() {
        _clocksList.apply {
            value?.clear()
            postValue(value)
        }
        CoroutineScope(Dispatchers.IO).launch {
            removeAllUseCase()
        }

    }

}