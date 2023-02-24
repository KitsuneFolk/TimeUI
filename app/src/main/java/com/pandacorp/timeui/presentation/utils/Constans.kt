package com.pandacorp.timeui.presentation.utils

sealed class Constans {
    object PreferencesKeys {
        const val languagesKey = "Languages"
        const val themesKey = "Themes"
        const val versionKey = "Version"
        
        const val preferenceBundleKey = "preferenceBundleKey"
    }
    
    // Item touch helper object with keys to detect what adapter's viewholder use
    object ITHKey {
        const val TIMER = 1
        const val STOPWATCH = 2
    }
    
    object Room {
        const val NAME = "myDatabase"
    }
    
    companion object {
        // bundle key if value is stored in isolation and no need to create other keys
        const val valueKey = "valueKey"
        
        // Key for intent to put and get serializable StopwatchItem or TimerItem
        const val IntentItem = "IntentItem"
        
        // Key for intent to put and get position of StopwatchItem or TimerItem
        const val IntentItemPosition = "IntentItemPosition"
    }
}