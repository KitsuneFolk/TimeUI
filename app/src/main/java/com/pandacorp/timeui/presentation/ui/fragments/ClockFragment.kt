package com.pandacorp.timeui.presentation.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentClockBinding
import com.pandacorp.timeui.presentation.ui.MainActivity
import com.pandacorp.timeui.presentation.ui.adapters.clocks.ClockAdapter
import com.pandacorp.timeui.presentation.utils.Constants
import com.pandacorp.timeui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.timeui.presentation.utils.Utils
import com.pandacorp.timeui.presentation.utils.fragulaNavController
import com.pandacorp.timeui.presentation.utils.viewBinding
import com.pandacorp.timeui.presentation.vm.ClockViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ClockFragment : DaggerFragment(R.layout.fragment_clock) {
    private val binding by viewBinding(FragmentClockBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ClockViewModel>({ activity as MainActivity }) { viewModelFactory }

    private val clockAdapter by lazy {
        ClockAdapter(this@ClockFragment.requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.clocksList.observe(viewLifecycleOwner) {
            clockAdapter.submitList(it)
        }
    }

    private fun initViews() {
        binding.clockRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = clockAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )

            val itemTouchHelper = CustomItemTouchHelper(
                requireContext(),
                Constants.ITHKey.CLOCK,
                object : CustomItemTouchHelper.ItemTouchHelperListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder is ClockAdapter.ViewHolder) {
                            viewModel.removeAt(viewHolder.adapterPosition)
                            Utils.handleShowingFAB(binding.clockRV, binding.addFab)
                        }
                    }
                })
            itemTouchHelper.isRoundCornersEnabled = false
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(this)
        }

        binding.addFab.setOnClickListener {
            fragulaNavController.navigate(R.id.nav_clock_screen)
        }
    }
}
