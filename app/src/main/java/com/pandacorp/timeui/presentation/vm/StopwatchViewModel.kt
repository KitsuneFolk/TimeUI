package com.pandacorp.timeui.presentation.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.domain.usecases.stopwatch.*
import com.pandacorp.timeui.presentation.ui.stopwatch.StopWatchFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StopwatchViewModel @Inject constructor(
    private val getAllUseCase: GetStopwatchesUseCase,
    private val addUseCase: AddStopwatchUseCase,
    private val removeUseCase: RemoveStopwatchUseCase,
    private val removeAllUseCase: RemoveAllStopwatchesUseCase,
    private val updateUseCase: UpdateStopwatchUseCase,
    private val updateAllUseCase: UpdateAllStopwatchesUseCase
) :
    ViewModel() {
    private val TAG = StopWatchFragment.TAG
    private val _stopwatchesList = MutableLiveData<MutableList<StopwatchItem>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getAllUseCase().apply {
                    postValue(this)
                }
            }
        }
    }
    val stopwatchesList: LiveData<MutableList<StopwatchItem>> = _stopwatchesList
    
    fun addItem(stopwatchItem: StopwatchItem) {
        Log.d(TAG, "addItem:")
        _stopwatchesList.value?.add(0, stopwatchItem)
        _stopwatchesList.postValue(_stopwatchesList.value)
        CoroutineScope(Dispatchers.IO).launch { addUseCase(stopwatchItem) }
    }
    
    fun removeItem(stopwatchItem: StopwatchItem) {
        _stopwatchesList.value?.remove(stopwatchItem)
        _stopwatchesList.postValue(_stopwatchesList.value)
        
        CoroutineScope(Dispatchers.IO).launch { removeUseCase(stopwatchItem) }
    }
    
    fun removeItemAt(position: Int): Boolean {
        Log.d(TAG, "removeItemAt: position = $position")
        removeItem(_stopwatchesList.value?.get(position) ?: return false)
        return true
    }
    
    
    fun removeAll() {
        _stopwatchesList.value?.clear()
        _stopwatchesList.postValue(_stopwatchesList.value)
        CoroutineScope(Dispatchers.IO).launch {
            removeAllUseCase()
        }
        
    }
    
    fun updateAll(stopwatches: MutableList<StopwatchItem>) {
        _stopwatchesList.postValue(stopwatches)
        CoroutineScope(Dispatchers.IO).launch {
            updateAllUseCase(stopwatches)
        }
        
    }
    
    fun updateItem(position: Int, stopwatchItem: StopwatchItem) {
        Log.d(
                TAG,
                "updateItem: position = $position, stopwatchItem.status = ${stopwatchItem.status}")
        _stopwatchesList.value?.set(position, stopwatchItem)
        _stopwatchesList.postValue(_stopwatchesList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateUseCase(stopwatchItem)
        }
        
    }
}