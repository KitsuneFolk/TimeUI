package com.pandacorp.timeui.presentation.ui.screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ScreenMainBinding
import com.pandacorp.timeui.presentation.utils.fragulaNavController
import com.pandacorp.timeui.presentation.utils.viewBinding

class MainScreen : Fragment(R.layout.screen_main) {
    private val binding by viewBinding(ScreenMainBinding::bind)

    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        setTitle(binding.navView.selectedItemId)

        binding.toolbarInclude.toolbar.apply {
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    inflateMenu(R.menu.menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.settings -> fragulaNavController.navigate(R.id.nav_settings_screen)
                    }
                    return true
                }
            }, viewLifecycleOwner)
        }

        binding.navView.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener {
                navigateFragment(it.itemId)
                true
            }
        }
    }

    private fun setTitle(itemId: Int) {
        val stringId = when (itemId) {
            R.id.nav_clocks -> R.string.clocks
            R.id.nav_timer -> R.string.timer
            R.id.nav_stopwatch -> R.string.stopwatch
            else -> throw IllegalArgumentException("Unknown fragment id $id")
        }
        binding.toolbarInclude.toolbar.setTitle(stringId)
    }

    private fun navigateFragment(itemId: Int) {
        setTitle(itemId)
        navController.navigate(itemId)
    }
}