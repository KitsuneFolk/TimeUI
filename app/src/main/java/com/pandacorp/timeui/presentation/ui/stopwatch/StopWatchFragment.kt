package com.pandacorp.timeui.presentation.ui.stopwatch

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentStopwatchBinding
import com.pandacorp.timeui.domain.models.StopwatchItem
import com.pandacorp.timeui.presentation.ui.stopwatch.adapter.StopwatchAdapter
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.timeui.presentation.utils.Utils
import com.pandacorp.timeui.presentation.vm.StopwatchViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class StopWatchFragment : Fragment() {
    companion object {
        const val TAG = "StopWatchFragment"
    }
    
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    
    private val vm: StopwatchViewModel by viewModels {
        viewModelFactory
    }
    
    private lateinit var binding: FragmentStopwatchBinding
    
    private lateinit var customAdapter: StopwatchAdapter
    
    private var onStopwatchClickedResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data -> // If it is null, then user didn't click any button
                val position = data.getIntExtra(Constans.IntentItemPosition, -1)
                val stopwatchItem = data.getSerializableExtra(Constans.IntentItem) as StopwatchItem
                vm.updateItem(position, stopwatchItem)
            }
        }
    
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Utils.setupExceptionHandler()
        binding = FragmentStopwatchBinding.inflate(inflater)
        
        checkIsFirstTime()
        initViews()
        
        return binding.root
    }
    
    private fun initViews() {
        initRecyclerView()
        binding.stopwatchAddFab.apply {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    requireContext(),
                    R.animator.increase_size_normal_animator)
            
            setOnClickListener {
                vm.addItem(StopwatchItem.create())
            }
        }
    }
    
    private fun initRecyclerView() {
        customAdapter =
            StopwatchAdapter(this@StopWatchFragment.requireActivity())
        customAdapter.setStopwatchListener(object : StopwatchAdapter.StopwatchListener {
            override fun onStopwatchRemove(viewHolder: StopwatchAdapter.ViewHolder, position: Int) {
                viewHolder.stopwatch.cancel()
                vm.removeItemAt(position)
            }
            
            override fun onStopwatchUpdate(position: Int, stopwatchItem: StopwatchItem) {
                vm.updateItem(position, stopwatchItem)
            }
            
            
            override fun onStopwatchClicked():
                    ActivityResultLauncher<Intent> = onStopwatchClickedResultLauncher
            
        })
        binding.stopwatchRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.stopwatchRecyclerView.adapter = customAdapter
        
        vm.stopwatchesList.observe(viewLifecycleOwner) {
            customAdapter.submitList(it)
        }
        enableSwipe()
        
        registerForContextMenu(binding.stopwatchRecyclerView)
        
    }
    
    private fun checkIsFirstTime() {
        //If app opened first time then add one stopwatch for user.
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (sp.getBoolean("isStopWatchFirstTime", true)) {
            val edit = sp.edit()
            val stopwatchItem = StopwatchItem.create()
            vm.addItem(stopwatchItem)
            edit.putBoolean("isStopWatchFirstTime", false)
            edit.apply()
        }
    }
    
    private fun enableSwipe() {
        //Attached the ItemTouchHelper
        binding.stopwatchRecyclerView.addItemDecoration(
                DividerItemDecoration(
                        binding.stopwatchRecyclerView.context,
                        DividerItemDecoration.VERTICAL
                )
        )
        
        val ith = CustomItemTouchHelper(
                requireContext(),
                Constans.ITHKey.STOPWATCH,
                object : CustomItemTouchHelper.OnTouchListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder is StopwatchAdapter.ViewHolder) {
                            viewHolder.stopwatch.cancel()
                            val position = viewHolder.adapterPosition
                            vm.removeItemAt(position)
                        }
                    }
                    
                })
        ItemTouchHelper(ith).attachToRecyclerView(binding.stopwatchRecyclerView)
    }
    
    override fun onDestroy() {
        // Get all active StopwatchView's from adapter and cancel them
        customAdapter.currentList.forEachIndexed { i, stopwatchItem ->
            val viewHolder = binding.stopwatchRecyclerView.findViewHolderForAdapterPosition(i)
            if (viewHolder != null) {
                (viewHolder as StopwatchAdapter.ViewHolder).stopwatch.cancel()
            }
        
        }
        super.onDestroy()
    }
}