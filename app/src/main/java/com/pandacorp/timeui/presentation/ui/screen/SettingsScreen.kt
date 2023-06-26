package com.pandacorp.timeui.presentation.ui.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ScreenSettingsBinding
import com.pandacorp.timeui.presentation.utils.Constants
import com.pandacorp.timeui.presentation.utils.dialogs.DialogListView
import com.pandacorp.timeui.presentation.utils.getPackageInfoCompat
import com.pandacorp.timeui.presentation.utils.viewBinding

class SettingsScreen : Fragment(R.layout.screen_settings) {
    private val sp by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    private val binding by viewBinding(ScreenSettingsBinding::bind)

    private val languageDialog by lazy {
        DialogListView(requireActivity(), Constants.PreferencesKeys.LANGUAGE).apply {
            onValueAppliedListener = {
                requireActivity().recreate()
            }
        }
    }
    private val themeDialog by lazy {
        DialogListView(requireActivity(), Constants.PreferencesKeys.THEME).apply {
            onValueAppliedListener = {
                requireActivity().recreate()
            }
        }
    }

    private fun initViews() {
        binding.toolbarInclude.toolbar.apply {
            setTitle(R.string.settings)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        // Retrieve the version from build.gradle and assign it to the TextView
        binding.versionTextView.apply {
            val version =
                requireContext().packageManager.getPackageInfoCompat(requireContext().packageName).versionName
            text = resources.getString(R.string.version, version)
        }

        binding.themeLayout.apply {
            setOnClickListener {
                if (isDialogShown()) return@setOnClickListener
                themeDialog.show()
            }
            binding.themeTextView.apply {
                val themeKey = sp.getString(
                    Constants.PreferencesKeys.THEME,
                    requireContext().resources.getString(R.string.settings_theme_default_value)
                )!!
                text = getThemeFromKey(themeKey)
            }
        }
        binding.languageLayout.apply {
            binding.languageTextView.apply {
                val languageKey = sp.getString(
                    Constants.PreferencesKeys.LANGUAGE,
                    requireContext().resources.getString(R.string.settings_language_default_value)
                )!!
                text = getLanguageFromKey(languageKey)
            }
            setOnClickListener {
                if (isDialogShown()) return@setOnClickListener
                languageDialog.show()
            }
        }
    }

    private fun getThemeFromKey(key: String): String {
        val themes = resources.getStringArray(R.array.Themes)
        val keys = resources.getStringArray(R.array.Themes_values)

        val index = keys.indexOf(key)
        return themes[index]
    }

    private fun getLanguageFromKey(key: String): String {
        val languages = resources.getStringArray(R.array.Languages)
        val keys = resources.getStringArray(R.array.Languages_values)

        val index = keys.indexOf(key)
        return languages[index]
    }

    private fun isDialogShown(): Boolean =
        (languageDialog.isShowing || themeDialog.isShowing)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val dialogKey = when {
            themeDialog.isShowing -> Constants.PreferencesKeys.THEME
            languageDialog.isShowing -> Constants.PreferencesKeys.LANGUAGE
            else -> null
        }

        outState.apply {
            putString(Constants.PreferencesKeys.SHOWED_DIALOG, dialogKey)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        when (savedInstanceState?.getString(Constants.PreferencesKeys.SHOWED_DIALOG, null)) {
            Constants.PreferencesKeys.THEME -> themeDialog.show()
            Constants.PreferencesKeys.LANGUAGE -> languageDialog.show()
        }
    }

    override fun onDestroy() {
        themeDialog.dismiss()
        languageDialog.dismiss()
        super.onDestroy()
    }
}