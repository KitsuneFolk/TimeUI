package com.pandacorp.timeui.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.domain.usecases.timer.AddTimerUseCase
import com.pandacorp.timeui.domain.usecases.timer.GetTimersUseCase
import com.pandacorp.timeui.domain.usecases.timer.RemoveAllTimersUseCase
import com.pandacorp.timeui.domain.usecases.timer.RemoveTimerUseCase
import com.pandacorp.timeui.domain.usecases.timer.UpdateAllTimersUseCase
import com.pandacorp.timeui.domain.usecases.timer.UpdateTimerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimerViewModel @Inject constructor(
    private val getTimersUseCase: GetTimersUseCase,
    private val addItemUseCase: AddTimerUseCase,
    private val removeTimerUseCase: RemoveTimerUseCase,
    private val removeAllTimersUseCase: RemoveAllTimersUseCase,
    private val updateTimerUseCase: UpdateTimerUseCase,
    private val updateAllTimersUseCase: UpdateAllTimersUseCase
) : ViewModel() {

    // The current TimerItem to transfer to TimerScreen
    private var _timerItem = MutableLiveData<TimerItem>()
    val timerItem get() = _timerItem.value!!.copy() // Get a copy to fix the bug when an item is not updated in the adapter

    private val _timersList = MutableLiveData<MutableList<TimerItem>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getTimersUseCase().apply {
                    postValue(this)
                }
            }
        }
    }
    val timersList: LiveData<MutableList<TimerItem>> = _timersList

    fun addItem(timerItem: TimerItem) {
        CoroutineScope(Dispatchers.IO).launch {
            timerItem.id = addItemUseCase(timerItem)
            _timersList.value?.add(0, timerItem)
            _timersList.postValue(_timersList.value)
        }
    }

    fun removeItemAt(position: Int) {
        val timerItem = _timersList.value?.get(position) ?: return
        _timersList.value?.remove(timerItem)
        _timersList.postValue(_timersList.value)

        CoroutineScope(Dispatchers.IO).launch {
            removeTimerUseCase(timerItem)
        }
    }


    fun removeAll() {
        _timersList.value?.clear()
        _timersList.postValue(_timersList.value)
        CoroutineScope(Dispatchers.IO).launch {
            removeAllTimersUseCase()
        }
    }

    fun updateAll(timers: MutableList<TimerItem>) {
        _timersList.value = timers
        _timersList.postValue(timers)
        CoroutineScope(Dispatchers.IO).launch {
            updateAllTimersUseCase(timers)
        }
    }

    fun updateItem(timerItem: TimerItem) {
        val position = _timersList.value?.indexOfFirst { it.id == timerItem.id } ?: return

        _timerItem.value = timerItem
        _timersList.value?.set(position, timerItem)
        _timersList.postValue(_timersList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateTimerUseCase(timerItem)
        }
    }
}