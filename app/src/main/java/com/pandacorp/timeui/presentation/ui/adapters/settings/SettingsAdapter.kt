package com.pandacorp.timeui.presentation.ui.adapters.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.presentation.utils.Constants

class SettingsAdapter(
    context: Context, languagesList: MutableList<SettingsItem>, private val preferenceKey: String
) : ArrayAdapter<SettingsItem>(context, 0, languagesList) {
    private var onListItemClickListener: OnListItemClickListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false)!!
        val listItem = getItem(position)!!
        view.apply {
            findViewById<ImageView>(R.id.imageView).apply {
                setImageDrawable(listItem.drawable)
            }
            view.setOnClickListener {
                onListItemClickListener?.onClick(listItem)
            }
            findViewById<TextView>(R.id.button).apply {
                text = listItem.title
            }
            findViewById<MaterialCardView>(R.id.cardView).apply {
                if (preferenceKey == Constants.PreferencesKeys.THEME) radius = 80f
            }
        }
        return view
    }

    fun setOnClickListener(onListItemClickListener: OnListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener
    }

    fun interface OnListItemClickListener {
        fun onClick(settingsItem: SettingsItem)
    }
}
    
