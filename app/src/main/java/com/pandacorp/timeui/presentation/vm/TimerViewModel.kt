package com.pandacorp.timeui.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.domain.usecases.timer.*
import com.pandacorp.timeui.presentation.ui.timer.TimerFragment
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
) :
    ViewModel() {
    companion object {
        private const val TAG = TimerFragment.TAG
    }
    
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
            val id = addItemUseCase(timerItem)
            timerItem.id = id
            _timersList.value?.add(0, timerItem)
            _timersList.postValue(_timersList.value)
        }
    }
    
    fun removeItem(timerItem: TimerItem) {
        _timersList.value?.remove(timerItem)
        _timersList.postValue(_timersList.value)
        
        CoroutineScope(Dispatchers.IO).launch { removeTimerUseCase(timerItem) }
    }
    
    fun removeItemAt(position: Int): Boolean {
        removeItem(_timersList.value?.get(position) ?: return false)
        return true
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
    
    fun updateItem(position: Int, timerItem: TimerItem) {
        _timersList.value?.set(position, timerItem)
        _timersList.postValue(_timersList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateTimerUseCase(timerItem)
        }
        
    }
}