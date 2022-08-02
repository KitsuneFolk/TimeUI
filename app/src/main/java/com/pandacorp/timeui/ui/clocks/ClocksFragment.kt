package com.pandacorp.timeui.ui.clocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.R
import java.text.SimpleDateFormat
import java.util.*

class ClocksFragment : Fragment() {
    private lateinit var root: View

    private lateinit var dateTv: TextView

    private lateinit var calendar: Calendar
    private lateinit var time: String
    private lateinit var date: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Setting the app name for the action_bar
        root = inflater.inflate(R.layout.fragment_clocks, container, false)

        initViews()

        return root
    }
    private fun initViews(){
        dateTv = root.findViewById(R.id.date_tv)

        setDate()

    }

    private fun setDate(){
        val today = Calendar.getInstance().time //getting date

        val formatter = SimpleDateFormat("dd.MM.yyyy") //formating according to my need

        date = formatter.format(today)
        dateTv.text = date

    }


}
