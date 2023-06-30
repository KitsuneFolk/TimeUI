package com.pandacorp.timeui.presentation.ui.screen

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ScreenTimezoneBinding
import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.ui.adapters.clocks.TimeZoneAdapter
import com.pandacorp.timeui.presentation.utils.Utils
import com.pandacorp.timeui.presentation.utils.fragulaNavController
import com.pandacorp.timeui.presentation.utils.viewBinding
import com.pandacorp.timeui.presentation.vm.ClockViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class TimeZoneScreen : DaggerFragment(R.layout.screen_timezone) {
    private val binding by viewBinding(ScreenTimezoneBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ClockViewModel>({ activity as MainActivity }) { viewModelFactory }

    private val timeZoneAdapter by lazy {
        TimeZoneAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.toolbarInclude.toolbar.apply {
            setTitle(R.string.selectTimeZone)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.ok.setOnClickListener {
            timeZoneAdapter.selectedPosition?.let {
                val clockItem = Utils.clocksList[it]
                if ((viewModel.clocksList.value?.any { item -> item.timeZone == clockItem.timeZone } != true)) {
                    viewModel.addItem(clockItem)
                    fragulaNavController.popBackStack()
                } else {
                    val snackbar =
                        Snackbar.make(binding.ok, R.string.clockAlreadyAdded, Snackbar.LENGTH_LONG)
                    snackbar.anchorView = binding.ok
                    snackbar.setTextColor(Color.WHITE)
                    snackbar.show()
                }
            }
        }

        timeZoneAdapter.submitList(Utils.clocksList)

        binding.recyclerView.adapter = timeZoneAdapter
    }
}