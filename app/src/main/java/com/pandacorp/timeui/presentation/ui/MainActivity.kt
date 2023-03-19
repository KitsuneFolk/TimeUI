package com.pandacorp.timeui.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ActivityMainBinding
import com.pandacorp.timeui.presentation.di.app.App
import com.pandacorp.timeui.presentation.ui.settings.SettingsActivity
import com.pandacorp.timeui.presentation.utils.PreferenceHandler
import dagger.android.AndroidInjection


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    
    private val preferencesResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(0, com.pandacorp.timeui.R.anim.slide_out_right)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        PreferenceHandler(this).load()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbarInclude.toolbar)
    
        val navController: NavController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController)
    
        val fragmentId = (application as App).fragmentId
        if (fragmentId != 0) binding.navView.selectedItemId = fragmentId
    
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.pandacorp.timeui.R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.pandacorp.timeui.R.id.menu_settings -> {
                preferencesResultLauncher.launch(
                        Intent(this@MainActivity, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onStop() {
        (application as App).fragmentId = binding.navView.selectedItemId
        super.onStop()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
