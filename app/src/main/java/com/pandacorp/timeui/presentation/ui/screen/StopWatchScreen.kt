package com.pandacorp.timeui.presentation.ui.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.ScreenStopwatchBinding
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.utils.viewBinding
import com.pandacorp.timeui.presentation.vm.StopwatchViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class StopWatchScreen : DaggerFragment(R.layout.screen_stopwatch) {
    private val binding by viewBinding(ScreenStopwatchBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<StopwatchViewModel>({ activity as MainActivity }) { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        checkStatus()

        binding.toolbarInclude.toolbar.apply {
            setTitle(R.string.stopwatch)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.startButton.setOnClickListener {
            val stopwatchItem = viewModel.stopwatchItem
            when (stopwatchItem.status) {
                StopwatchItem.RUNNING -> {
                    binding.stopButton.visibility = View.VISIBLE
                    binding.resetButton.visibility = View.GONE

                    return@setOnClickListener
                }

                StopwatchItem.ADDED -> binding.stopwatchView.start()

                StopwatchItem.RESET -> binding.stopwatchView.start()

                StopwatchItem.STOPPED -> binding.stopwatchView.start(stopwatchItem.stopTime)
            }
            binding.stopButton.visibility = View.VISIBLE
            binding.resetButton.visibility = View.GONE

            stopwatchItem.apply {
                status = StopwatchItem.RUNNING
                startSysTime = System.currentTimeMillis() - stopTime
            }
            viewModel.updateItem(stopwatchItem = stopwatchItem)
        }

        binding.stopButton.setOnClickListener {
            binding.stopwatchView.cancel()

            binding.stopButton.visibility = View.GONE
            binding.resetButton.visibility = View.VISIBLE

            val stopwatchItem = viewModel.stopwatchItem.apply {
                stopTime = binding.stopwatchView.getTime()
                status = StopwatchItem.STOPPED
            }
            viewModel.updateItem(stopwatchItem)
        }

        binding.resetButton.setOnClickListener {
            binding.stopwatchView.cancel()
            binding.stopwatchView.setTime(StopwatchItem.START_TIME)

            binding.stopButton.visibility = View.VISIBLE
            binding.resetButton.visibility = View.GONE

            val stopwatchItem = viewModel.stopwatchItem.apply {
                stopTime = StopwatchItem.START_TIME
                status = StopwatchItem.RESET
            }
            viewModel.updateItem(stopwatchItem)
        }

    }

    private fun checkStatus() {
        val stopwatchItem = viewModel.stopwatchItem
        when (stopwatchItem.status) {
            StopwatchItem.ADDED -> binding.stopwatchView.setTime(StopwatchItem.START_TIME)

            StopwatchItem.STOPPED -> {
                binding.stopwatchView.cancel()
                binding.stopwatchView.setTime(stopwatchItem.stopTime)

                binding.stopButton.visibility = View.GONE
                binding.resetButton.visibility = View.VISIBLE
            }

            StopwatchItem.RUNNING -> {
                binding.stopwatchView.start(System.currentTimeMillis() - stopwatchItem.startSysTime)

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }

            StopwatchItem.RESET -> {
                binding.stopwatchView.cancel()
                binding.stopwatchView.setTime(0)

                binding.stopButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.GONE
            }
        }

    }

    override fun onDestroy() {
        binding.stopwatchView.cancel()
        super.onDestroy()
    }
}