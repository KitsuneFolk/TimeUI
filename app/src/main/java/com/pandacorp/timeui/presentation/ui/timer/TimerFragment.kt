package com.pandacorp.timeui.presentation.ui.timer

import android.animation.AnimatorInflater
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikovac.timepickerwithseconds.TimePicker
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentTimerBinding
import com.pandacorp.timeui.domain.models.TimerItem
import com.pandacorp.timeui.presentation.ui.timer.adapter.TimerAdapter
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.timeui.presentation.utils.Utils
import com.pandacorp.timeui.presentation.vm.TimerViewModel
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import javax.inject.Inject


class TimerFragment : DaggerFragment() {
    companion object {
        const val TAG = "TimerFragment"
    }
    
    private lateinit var binding: FragmentTimerBinding
    
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    
    private val vm: TimerViewModel by viewModels {
        viewModelFactory
    }
    
    private lateinit var customAdapter: TimerAdapter
    
    private var onTimerClickedResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data -> // If it is null, then user didn't click any button
                val position = data.getIntExtra(Constans.IntentItemPosition, -1)
                val timerItem = data.getSerializableExtra(Constans.IntentItem) as TimerItem
                
                vm.updateItem(position = position, timerItem = timerItem)
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
        binding = FragmentTimerBinding.inflate(inflater)
        
        initViews()
        checkIsFirstTime()
        
        return binding.root
    }
    
    private fun initViews() {
        customAdapter = TimerAdapter(this@TimerFragment.requireActivity())
        customAdapter.setTimerListener(object : TimerAdapter.TimerListener {
            override fun onTimerRemove(
                viewHolder: TimerAdapter.ViewHolder,
                position: Int
            ) {
                viewHolder.countdown.stop()
                vm.removeItemAt(position)
            }
            
            override fun onTimerUpdate(position: Int, timerItem: TimerItem) {
                vm.updateItem(position, timerItem)
            }
            
            override fun onTimerClicked(): ActivityResultLauncher<Intent> =
                onTimerClickedResultLauncher
            
        })
        binding.timerRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.timerRecyclerView.adapter = customAdapter
        
        binding.timerRecyclerView.addItemDecoration(
                DividerItemDecoration(
                        binding.timerRecyclerView.context,
                        DividerItemDecoration.VERTICAL
                )
        )
        val ith = CustomItemTouchHelper(
                requireContext(),
                Constans.ITHKey.TIMER,
                object : CustomItemTouchHelper.OnTouchListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder is TimerAdapter.ViewHolder) {
                            viewHolder.countdown.stop()
                            val position = viewHolder.adapterPosition
                            vm.removeItemAt(position)
                        }
                    }
                    
                })
        ItemTouchHelper(ith).attachToRecyclerView(binding.timerRecyclerView)
        
        registerForContextMenu(binding.timerRecyclerView)
        
        val dialog = initDialog()
        binding.timerAddFab.setOnClickListener { dialog.show() }
        binding.timerAddFab.stateListAnimator = AnimatorInflater.loadStateListAnimator(
                requireContext(),
                R.animator.increase_size_normal_animator)
        
        
        vm.timersList.observe(viewLifecycleOwner) {
            customAdapter.submitList(it)
        }
        
        
    }
    
    private fun initDialog(): Dialog {
        val dialog = Dialog(requireActivity())
        
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_time_picker, null)
        val dialogTimePicker = view.findViewById<TimePicker>(R.id.dialogTimePicker)
        val dialogTimePickerOk = view.findViewById<Button>(R.id.dialogTimePickerOk)
        val dialogTimePickerCancel = view.findViewById<Button>(R.id.dialogTimePickerCancel)
        dialogTimePicker.setIs24HourView(true)
        dialogTimePicker.setCurrentSecond(0)
        dialogTimePicker.currentMinute = 5
        dialogTimePicker.currentHour = 0
        
        dialogTimePickerOk.setOnClickListener {
            val startTime = Utils.timeToTimeInMillis(
                    dialogTimePicker.currentHour,
                    dialogTimePicker.currentMinute,
                    dialogTimePicker.currentSeconds
            )
            val status = TimerItem.ADDED
            
            val timerItem =
                TimerItem(startTime = startTime, currentTime = startTime, status = status)
            vm.add(timerItem)
            dialog.dismiss()
        }
        dialogTimePickerCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Remove background
        dialog.setContentView(view)
        
        return dialog
    }
    
    private fun checkIsFirstTime() {
        //If app opened first time then add one test timer for user.
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (sp.getBoolean("isTimerFirstTime", true)) {
            val edit = sp.edit()
            val startTime = (5 * 60 * 1000).toLong()
            val timerItem =
                TimerItem(startTime = startTime, currentTime = startTime, status = TimerItem.ADDED)
            vm.add(timerItem)
            edit.putBoolean("isTimerFirstTime", false)
            edit.apply()
        }
    }
    
    override fun onDestroy() {
        // Get all active CountdownView's from adapter and cancel them
        customAdapter.currentList.forEachIndexed { i, _ ->
            val viewHolder = binding.timerRecyclerView.findViewHolderForAdapterPosition(i)
            if (viewHolder != null) {
                (viewHolder as TimerAdapter.ViewHolder).countdown.stop()
            }
        
        }
        super.onDestroy()
    }
}