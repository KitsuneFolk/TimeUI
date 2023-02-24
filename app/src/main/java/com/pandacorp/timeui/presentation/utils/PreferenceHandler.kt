package com.pandacorp.timeui.presentation.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import com.pandacorp.timeui.R
import java.util.*

class PreferenceHandler(private val context: Context) {
    private val TAG = "Utils"
    
    private val themeFollowSystem = "follow_system"
    private val themeBlue = "blue"
    private val themeDark = "dark"
    private val themeRed = "red"
    private val themePurple = "purple"
    
    private val themeDefault = themeFollowSystem
    
    private val russianLocale = Locale("ru")
    private val englishLocale = Locale("en")
    private val ukrainianLocale = Locale("uk")
    
    private var sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    
    fun load() {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val theme = sp.getString(Constans.PreferencesKeys.themesKey, themeDefault)!!
        val language = sp.getString(Constans.PreferencesKeys.languagesKey, "")!!
        setMyTheme(context, theme)
        setMyLanguage(context, language)
    }
    
    private fun setMyTheme(context: Context, theme: String) {
        when (theme) {
            themeFollowSystem -> {
                if (isDeviceDarkMode()) context.setTheme(R.style.DarkTheme)
                else context.setTheme(R.style.BlueTheme)
                
            }
            themeBlue -> context.setTheme(R.style.BlueTheme)
            themeDark -> context.setTheme(R.style.DarkTheme)
            themeRed -> context.setTheme(R.style.RedTheme)
            themePurple -> context.setTheme(R.style.PurpleTheme)
            
        }
    }
    
    private fun setMyLanguage(context: Context, language: String) {
        val configuration = Configuration()
        when (language) {
            "ru" -> {
                Locale.setDefault(russianLocale)
                configuration.setLocale(russianLocale)
            }
            "en" -> {
                Locale.setDefault(englishLocale)
                configuration.setLocale(englishLocale)
            }
            "uk" -> {
                Locale.setDefault(ukrainianLocale)
                configuration.setLocale(ukrainianLocale)
            }
        }
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }
    
    private fun isDeviceDarkMode(): Boolean =
        (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    
}