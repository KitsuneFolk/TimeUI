package com.pandacorp.timeui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pandacorp.timeui.settings.MySettings
import com.pandacorp.timeui.settings.SettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    
    private val TAG = "MyLogs"
    
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MySettings(this).start()
        setContentView(R.layout.activity_main)
        
        CoroutineScope(Dispatchers.Main).launch {
            initViews()
            
        }
        
    }
    
    private fun initViews() {
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        
        
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
            }
        }
        
        return true
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
            
        }
    }
    
    
}
