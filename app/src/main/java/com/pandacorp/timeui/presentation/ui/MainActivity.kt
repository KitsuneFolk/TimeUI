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
import com.pandacorp.timeui.presentation.utils.Utils
import dagger.android.AndroidInjection


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    var fragulaNavController: NavController? = null
    private var swipeController: SwipeController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        AndroidInjection.inject(this)
        Utils.setupExceptionHandler()
        super.onCreate(savedInstanceState)
        PreferenceHandler.load(this)
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
