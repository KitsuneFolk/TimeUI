package com.pandacorp.timeui.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.fragula2.animation.SwipeController
import com.fragula2.utils.findSwipeController
import com.pandacorp.timeui.databinding.ActivityMainBinding
import com.pandacorp.timeui.presentation.utils.PreferenceHandler
import dagger.android.AndroidInjection


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    var fragulaNavController: NavController? = null
    private var swipeController: SwipeController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        Thread.setDefaultUncaughtExceptionHandler { _, throwable -> throw (throwable) } // Throw any uncaught exceptions
        AndroidInjection.inject(this)
        PreferenceHandler.setLanguage(this)
        super.onCreate(savedInstanceState)
        PreferenceHandler.setTheme(this)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    override fun onDestroy() {
        _binding = null
        swipeController = null
        fragulaNavController = null
        super.onDestroy()
    }

    private fun initViews() {
        binding.fragulaNavHostFragment.getFragment<NavHostFragment>().apply {
            swipeController = findSwipeController()
            fragulaNavController = navController
        }
    }
}
