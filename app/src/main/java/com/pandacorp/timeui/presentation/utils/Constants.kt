package com.pandacorp.timeui.presentation.utils

sealed class Constants {
    object PreferencesKeys {
        const val LANGUAGE = "Languages"
        const val THEME = "Themes"
        const val SHOWED_DIALOG = "SHOWED_DIALOG"
    }

    // Item touch helper sealed class with keys to detect what adapter's viewholder use.
    sealed class ITHKey {
        object TIMER : ITHKey()
        object STOPWATCH : ITHKey()
        object CLOCK : ITHKey()
    }

    object Room {
        const val NAME = "myDatabase"
    }

    /**
     * Save the current active dialog in onSaveInstanceState and start it after rotation
     */
    object Dialog {
        const val SAVE_KEY = "activeDialog"
        const val TIMER_DIALOG = 1
    }

    companion object {
        // Key for intent to put and get parcelable StopwatchItem and TimerItem
        const val IntentItem = "IntentItem"

        const val SNACKBAR_DURATION = 3000

        const val ANIMATION_DURATION = 500L
    }
}