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
import com.pandacorp.timeui.presentation.ui.clock.ClockFragment
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
    companion object {
        private const val TAG = ClockFragment.TAG
    }
    
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
            _clocksList.value?.add(clockItem)
            _clocksList.postValue(_clocksList.value)
        }
    }
    
    fun removeItem(clockItem: ClockItem) {
        _clocksList.value?.remove(clockItem)
        _clocksList.postValue(_clocksList.value)
        
        CoroutineScope(Dispatchers.IO).launch { removeUseCase(clockItem) }
    }
    
    fun removeItemAt(position: Int): Boolean {
        removeItem(_clocksList.value?.get(position) ?: return false)
        return true
    }
    
    
    fun removeAll() {
        _clocksList.value?.clear()
        _clocksList.postValue(_clocksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            removeAllUseCase()
        }
        
    }
    
}