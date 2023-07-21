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
import com.pandacorp.timeui.databinding.FragmentStopwatchBinding
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.ui.adapters.StopwatchAdapter
import com.pandacorp.timeui.presentation.utils.Constants
import com.pandacorp.timeui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.timeui.presentation.utils.Utils
import com.pandacorp.timeui.presentation.utils.fragulaNavController
import com.pandacorp.timeui.presentation.utils.viewBinding
import com.pandacorp.timeui.presentation.vm.StopwatchViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class StopwatchFragment : DaggerFragment(R.layout.fragment_stopwatch) {
    private val binding by viewBinding(FragmentStopwatchBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<StopwatchViewModel>({ activity as MainActivity }) { viewModelFactory }

    private val stopwatchAdapter by lazy {
        StopwatchAdapter().apply {
            stopwatchListener = object : StopwatchAdapter.StopwatchListener {
                override fun onStopwatchRemove(position: Int) {
                    viewModel.removeItemAt(position)
                    Utils.handleShowingFAB(binding.recyclerView, binding.addFab)
                }

                override fun onStopwatchUpdate(stopwatchItem: StopwatchItem) {
                    viewModel.updateItem(stopwatchItem)
                }

                override fun onStopwatchClicked(stopwatchItem: StopwatchItem) {
                    viewModel.updateItem(stopwatchItem)
                    fragulaNavController.navigate(R.id.nav_stopwatch_screen)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        viewModel.stopwatchesList.observe(viewLifecycleOwner) {
            stopwatchAdapter.submitList(it)
            Handler(Looper.getMainLooper()).postDelayed({ // Add a delay for recyclerview to call onDetachedFromWindow
                if (it.isEmpty()) {
                    val transition = Fade().apply {
                        duration = Constants.ANIMATION_DURATION
                        addTarget(binding.recyclerView)
                        addTarget(binding.hintInclude.root)
                    }

                    TransitionManager.beginDelayedTransition(binding.root, transition)
                    binding.recyclerView.visibility = View.GONE
                    binding.hintInclude.root.visibility = View.VISIBLE
                } else {
                    if (binding.hintInclude.root.visibility != View.VISIBLE) return@postDelayed // skip, user just entered the fragment
                    val transition = Fade().apply {
                        duration = Constants.ANIMATION_DURATION
                        addTarget(binding.recyclerView)
                        addTarget(binding.hintInclude.root)
                    }

                    TransitionManager.beginDelayedTransition(binding.root, transition)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.hintInclude.root.visibility = View.GONE
                }
            }, 50)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stopwatchAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding.recyclerView.context,
                    DividerItemDecoration.VERTICAL
                )
            )
            val itemTouchHelper = CustomItemTouchHelper(
                requireContext(),
                Constants.ITHKey.STOPWATCH,
                object : CustomItemTouchHelper.ItemTouchHelperListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder is StopwatchAdapter.ViewHolder)
                            stopwatchAdapter.stopwatchListener!!.onStopwatchRemove(viewHolder.bindingAdapterPosition)
                    }
                })
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.recyclerView)
            registerForContextMenu(this)
        }

        binding.addFab.setOnClickListener {
            viewModel.addItem()
        }
    }

}