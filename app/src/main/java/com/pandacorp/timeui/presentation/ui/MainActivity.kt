package com.pandacorp.timeui.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ActivityMainBinding
import com.pandacorp.timeui.presentation.ui.settings.SettingsActivity
import com.pandacorp.timeui.presentation.utils.PreferenceHandler
import dagger.android.AndroidInjection

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    
    private lateinit var binding: ActivityMainBinding
    
    private val preferencesResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(0, R.anim.slide_out_right)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        PreferenceHandler(this).load()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbarInclude.toolbar)
        
        val navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                preferencesResultLauncher.launch(
                        Intent(this@MainActivity, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
        
    }
    
}
