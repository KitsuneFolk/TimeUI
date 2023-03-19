package com.pandacorp.timeui.presentation.ui.clock

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ActivityCountryBinding
import com.pandacorp.timeui.presentation.ui.stopwatch.adapter.CountryAdapter
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.PreferenceHandler
import com.pandacorp.timeui.presentation.utils.Utils

class CountryActivity : AppCompatActivity() {
    companion object {
        private const val TAG = ClockFragment.TAG
    }
    
    private var _binding: ActivityCountryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var countryAdapter: CountryAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceHandler(this).load()
        _binding = ActivityCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.countryToolbarInclude.toolbar)
        supportActionBar!!.setTitle(R.string.selectTimeZone)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        
        initViews(savedInstanceState)
    }
    
    private fun initViews(savedInstanceState: Bundle?) {
        binding.dialogCountryOK.setOnClickListener {
            if (countryAdapter.selectedPosition != RecyclerView.NO_POSITION) {
                val data = Intent()
                data.putExtra(Constans.IntentItemPosition, countryAdapter.selectedPosition)
                setResult(RESULT_OK, data)
                finish()
            }
            
        }
        
        countryAdapter = CountryAdapter(this)
        countryAdapter.selectedPosition =
            savedInstanceState?.getInt(Constans.SelectedPosition) ?: RecyclerView.NO_POSITION
        countryAdapter.submitList(Utils.clocksList)
        binding.dialogCountryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.dialogCountryRecyclerView.adapter = countryAdapter
        
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constans.SelectedPosition, countryAdapter.selectedPosition)
        super.onSaveInstanceState(outState)
    }
    
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}