package com.pandacorp.timeui.presentation.utils.dialogs

import android.content.Context
import android.os.Bundle
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.DialogListViewBinding
import com.pandacorp.timeui.presentation.ui.adapters.settings.SettingsAdapter
import com.pandacorp.timeui.presentation.ui.adapters.settings.SettingsItem
import com.pandacorp.timeui.presentation.utils.Constants

class DialogListView(private val context: Context, private val preferenceKey: String) : CustomDialog(context) {
    private var _binding: DialogListViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.title.setText(
            when (preferenceKey) {
                Constants.PreferencesKeys.THEME -> R.string.theme
                Constants.PreferencesKeys.LANGUAGE -> R.string.language
                else -> throw IllegalArgumentException("PreferenceKey = $preferenceKey")
            }
        )

        binding.cancel.setOnClickListener {
            cancel()
        }

        val itemsList: MutableList<SettingsItem> = when (preferenceKey) {
            Constants.PreferencesKeys.THEME -> fillThemesList()
            Constants.PreferencesKeys.LANGUAGE -> fillLanguagesList()
            else -> throw IllegalArgumentException()

        }
        val adapter = SettingsAdapter(context, itemsList, preferenceKey).apply {
            setOnClickListener { listItem ->
                val value = listItem.value
                if (sp.getString(preferenceKey, "") == value) return@setOnClickListener
                sp.edit().putString(preferenceKey, value).apply()
                onValueAppliedListener(value)
                cancel()
            }
        }
        binding.listView.adapter = adapter
    }

    private fun fillThemesList(): MutableList<SettingsItem> {
        val keysList = context.resources.getStringArray(R.array.Themes_values)
        val titlesList = context.resources.getStringArray(R.array.Themes)
        val itemsList =
            context.resources.obtainTypedArray(R.array.Themes_drawables)
        val themesList: MutableList<SettingsItem> = mutableListOf()
        repeat(keysList.size) { i ->
            themesList.add(
                SettingsItem(
                    keysList[i],
                    titlesList[i],
                    itemsList.getDrawable(i)!!
                )
            )
        }
        itemsList.recycle()
        return themesList
    }

    private fun fillLanguagesList(): MutableList<SettingsItem> {
        val keysList = context.resources.getStringArray(R.array.Languages_values)
        val drawablesList =
            context.resources.obtainTypedArray(R.array.Languages_drawables)
        val titlesList = context.resources.getStringArray(R.array.Languages)
        val itemsList: MutableList<SettingsItem> = mutableListOf()
        repeat(keysList.size) { i ->
            itemsList.add(
                SettingsItem(
                    keysList[i],
                    titlesList[i],
                    drawablesList.getDrawable(i)!!
                )
            )
        }
        drawablesList.recycle()
        return itemsList
    }

}