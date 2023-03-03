package com.pandacorp.timeui.presentation.utils

sealed class Constans {
    object PreferencesKeys {
        const val languagesKey = "Languages"
        const val themesKey = "Themes"
        const val versionKey = "Version"
        
        const val preferenceBundleKey = "preferenceBundleKey"
    }
    
    // Item touch helper sealed class with keys to detect what adapter's viewholder use.
    sealed class ITHKey {
        object TIMER : ITHKey()
        object STOPWATCH : ITHKey()
    }
    
    object Room {
        const val NAME = "myDatabase"
    }
    
    object FragmentKey {
        const val CLOCK = 0
        const val TIMER = 1
        const val STOPWATCH = 2
    }
    
    companion object {
        // bundle key if value is stored in isolation and no need to create other keys
        const val valueKey = "valueKey"
        
        // Key for intent to put and get serializable StopwatchItem or TimerItem
        const val IntentItem = "IntentItem"
        
        // Key for intent to put and get position of StopwatchItem or TimerItem
        const val IntentItemPosition = "IntentItemPosition"
        
        // Value for delay in fragments to properly draw transition and don't skip animation
        const val TIME_DELAY: Long = 100
        
    }
}