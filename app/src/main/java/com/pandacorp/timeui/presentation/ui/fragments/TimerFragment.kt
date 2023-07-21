package com.pandacorp.timeui.presentation.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentTimerBinding
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.ui.adapters.TimerAdapter
import com.pandacorp.timeui.presentation.utils.Constants
import com.pandacorp.timeui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.timeui.presentation.utils.Utils
import com.pandacorp.timeui.presentation.utils.dialogs.DialogTimer
import com.pandacorp.timeui.presentation.utils.fragulaNavController
import com.pandacorp.timeui.presentation.utils.viewBinding
import com.pandacorp.timeui.presentation.vm.TimerViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject


class TimerFragment : DaggerFragment(R.layout.fragment_timer) {
    private val binding by viewBinding(FragmentTimerBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TimerViewModel>({ activity as MainActivity }) { viewModelFactory }

    private val timerAdapter by lazy {
        TimerAdapter().apply {
            timerListener = object : TimerAdapter.TimerListener {
                override fun onTimerRemove(viewHolder: TimerAdapter.ViewHolder) {
                    viewModel.removeItemAt(viewHolder.bindingAdapterPosition)
                    Utils.handleShowingFAB(binding.timerRecyclerView, binding.addFab)
                }

                override fun onTimerUpdate(position: Int, timerItem: TimerItem) {
                    viewModel.updateItem(timerItem)
                }

                override fun onTimerClicked(position: Int, timerItem: TimerItem) {
                    viewModel.updateItem(timerItem.copy()) // Provide an object, but not a reference
                    fragulaNavController.navigate(R.id.nav_timer_screen)
                }
            }
        }
    }

    private val addDialog by lazy {
        DialogTimer(requireContext()).apply {
            onTimeAppliedListener = { hours, minutes, seconds ->
                val startTime = Utils.timeToTimeInMillis(hours, minutes, seconds)
                val timerItem =
                    TimerItem(startTime = startTime, currentTime = startTime, status = TimerItem.ADDED)
                viewModel.addItem(timerItem)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (addDialog.isShowing) outState.putInt(Constants.Dialog.SAVE_KEY, Constants.Dialog.TIMER_DIALOG)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            when (it.getInt(Constants.Dialog.SAVE_KEY)) {
                Constants.Dialog.TIMER_DIALOG -> addDialog.show()
            }
        }
        super.onViewStateRestored(savedInstanceState)
    }

    private fun initViews() {
        viewModel.timersList.observe(viewLifecycleOwner) {
            timerAdapter.submitList(it)

            Handler(Looper.getMainLooper()).postDelayed({ // Add a delay for recyclerview to call onDetachedFromWindow
                if (it.isEmpty()) {
                    val transition = Fade().apply {
                        duration = Constants.ANIMATION_DURATION
                        addTarget(binding.timerRecyclerView)
                        addTarget(binding.timerIncludeHint.root)
                    }

                    TransitionManager.beginDelayedTransition(binding.root, transition)
                    binding.timerRecyclerView.visibility = View.GONE
                    binding.timerIncludeHint.root.visibility = View.VISIBLE
                } else {
                    if (binding.timerIncludeHint.root.visibility != View.VISIBLE) return@postDelayed // Skip, user just entered the fragment
                    val transition = Fade().apply {
                        duration = Constants.ANIMATION_DURATION
                        addTarget(binding.timerRecyclerView)
                        addTarget(binding.timerIncludeHint.root)
                    }

                    TransitionManager.beginDelayedTransition(binding.root, transition)
                    binding.timerRecyclerView.visibility = View.VISIBLE
                    binding.timerIncludeHint.root.visibility = View.GONE
                }
            }, 50)
        }

        binding.timerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            val ith = CustomItemTouchHelper(
                requireContext(),
                Constants.ITHKey.TIMER,
                object : CustomItemTouchHelper.ItemTouchHelperListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder is TimerAdapter.ViewHolder)
                            timerAdapter.timerListener!!.onTimerRemove(viewHolder)
                    }
                })
            ItemTouchHelper(ith).attachToRecyclerView(this)

            registerForContextMenu(this)
        }

        binding.addFab.setOnClickListener {
            addDialog.show()
        }
    }
}