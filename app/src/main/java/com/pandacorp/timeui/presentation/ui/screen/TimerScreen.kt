package com.pandacorp.timeui.presentation.ui.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ScreenTimerBinding
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.utils.viewBinding
import com.pandacorp.timeui.presentation.vm.TimerViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class TimerScreen : DaggerFragment(R.layout.screen_timer) {
    private val binding by viewBinding(ScreenTimerBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TimerViewModel>({ activity as MainActivity }) { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onDestroyView() {
        binding.countdown.cancel()
        super.onDestroyView()
    }

    private fun initViews() {
        checkStatus()

        binding.toolbarInclude.toolbar.apply {
            setTitle(R.string.timer)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.startButton.setOnClickListener {
            val timerItem = viewModel.timerItem

            when (timerItem.status) {
                TimerItem.RUNNING -> {
                    binding.stopButton.visibility = View.VISIBLE
                    binding.resetButton.visibility = View.GONE
                    return@setOnClickListener
                }

                TimerItem.ADDED -> binding.countdown.start(timerItem.startTime)
                TimerItem.RESET -> binding.countdown.start(timerItem.startTime)
                TimerItem.STOPPED -> binding.countdown.start(timerItem.currentTime)
            }
            binding.stopButton.visibility = View.VISIBLE
            binding.resetButton.visibility = View.GONE

            timerItem.currentTime = binding.countdown.milliseconds + System.currentTimeMillis()
            timerItem.status = TimerItem.RUNNING

            viewModel.updateItem(timerItem)
        }

        binding.stopButton.setOnClickListener {
            binding.countdown.cancel()

            binding.stopButton.visibility = View.GONE
            binding.resetButton.visibility = View.VISIBLE

            val timerItem = viewModel.timerItem.apply {
                currentTime = binding.countdown.milliseconds
                status = TimerItem.STOPPED
            }

            viewModel.updateItem(timerItem)

        }

        binding.resetButton.setOnClickListener {
            val timerItem = viewModel.timerItem.apply {
                status = TimerItem.RESET
                currentTime = startTime
            }

            viewModel.updateItem(timerItem)

            binding.stopButton.visibility = View.VISIBLE
            binding.resetButton.visibility = View.GONE

            binding.countdown.cancel()
            binding.countdown.updateShow(timerItem.startTime)
        }
    }

    private fun checkStatus() {
        val timerItem = viewModel.timerItem
        when (timerItem.status) {
            TimerItem.ADDED -> binding.countdown.updateShow(timerItem.startTime)
            TimerItem.STOPPED -> {
                binding.countdown.cancel()
                binding.countdown.updateShow(timerItem.currentTime)

                binding.stopButton.visibility = View.GONE
                binding.resetButton.visibility = View.VISIBLE
            }

            TimerItem.RUNNING -> {
                binding.countdown.start(timerItem.currentTime - System.currentTimeMillis())

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }

            TimerItem.RESET -> {
                binding.countdown.cancel()
                binding.countdown.updateShow(timerItem.startTime)

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }
        }
    }
}