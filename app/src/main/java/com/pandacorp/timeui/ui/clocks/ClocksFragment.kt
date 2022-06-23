package com.pandacorp.timeui.ui.clocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.databinding.FragmentClocksBinding
import java.text.SimpleDateFormat
import java.util.*

class ClocksFragment : Fragment() {
    private lateinit var binding: FragmentClocksBinding

    private lateinit var calendar: Calendar
    private lateinit var time: String
    private lateinit var date: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Setting the app name for the action_bar

        binding = FragmentClocksBinding.inflate(inflater, container, false)

        initViews()

        return  binding.root
    }
    private fun initViews(){

        setDate()



    }

    private fun setDate(){
        val today = Calendar.getInstance().time //getting date

        val formatter = SimpleDateFormat("dd.MM.yyyy") //formating according to my need

        date = formatter.format(today)
        binding.dateTv.text = date

    }


}
