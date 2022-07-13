package com.pandacorp.timeui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.preference.PreferenceManager
import com.pandacorp.timeui.R
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

class MySettings(var context: Context) {
    private val TAG = "MyLogs"
    private lateinit var theme: String
    private lateinit var language: String
    private val sp: SharedPreferences
    init {
        sp = PreferenceManager.getDefaultSharedPreferences(context)
    }
    fun start() {
        //Обработка при первом запуске приложения, когда SharedPreferences ещё не созданы.
        theme = sp.getString("Themes", "blue")!!
        language = sp.getString("Languages", "")!!
        setMyTheme()
        setMyLanguage()
    }

    private fun setMyTheme() {
        when (theme) {
            "blue" -> context.setTheme(R.style.BlueTheme)
            "dark" -> context.setTheme(R.style.DarkTheme)
            "red" -> context.setTheme(R.style.RedTheme)
        }
    }

    private fun setMyLanguage() {
        val configuration = Configuration()
        when (language) {
            "ru" -> {
                val russian_locale = Locale("ru")
                Locale.setDefault(russian_locale)
                configuration.locale = russian_locale
                context.resources.updateConfiguration(configuration, null)

            }
            "en" -> {
                val english_locale = Locale("en")
                Locale.setDefault(english_locale)
                configuration.locale = english_locale
                context.resources.updateConfiguration(configuration, null)

            }
            "uk" -> {
                val ukrainian_locale = Locale("uk")
                Locale.setDefault(ukrainian_locale)
                configuration.locale = ukrainian_locale
                context.resources.updateConfiguration(configuration, null)

            }
        }

    }

    companion object {
        fun getBackgroundColor(context: Context): Int {

            val sp = context.defaultSharedPreferences

            val theme = when (sp.getString("Themes", "blue")!!) {
                "blue" -> R.color.BlueTheme_Background
                "dark" -> R.color.DarkTheme_Background
                "red" -> R.color.RedTheme_Background
                else -> {
                    throw Exception("Value cannot be else! ")
                }
            }
            return theme
        }

    }


}