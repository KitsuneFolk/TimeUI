package com.pandacorp.timeui.presentation.ui.clock

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pandacorp.timeui.R
import com.pandacorp.timeui.databinding.FragmentClockBinding
import com.pandacorp.timeui.domain.models.ClockItem
import com.pandacorp.timeui.presentation.utils.Constans
import com.pandacorp.timeui.presentation.utils.CustomItemTouchHelper
import com.pandacorp.timeui.presentation.utils.Utils
import com.pandacorp.timeui.presentation.vm.ClockViewModel
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject

class ClockFragment : Fragment() {
    companion object {
        const val TAG = "ClockFragment"
    }
    
    private var _binding: FragmentClockBinding? = null
    private val binding get() = _binding!!
    
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    
    private val vm: ClockViewModel by viewModels {
        viewModelFactory
    }
    
    private lateinit var customAdapter: ClockAdapter
    
    private val countriesResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val position =
                it.data!!.getIntExtra(Constans.IntentItemPosition, RecyclerView.NO_POSITION)
            val clockItem = Utils.clocksList[position]
            if ((vm.clocksList.value?.any { item ->
                    item.timeZoneId == clockItem.timeZoneId
                } != true)) {
                vm.addItem(clockItem)
            } else {
                val snackbar = Snackbar.make(
                        binding.clockAddFab,
                        R.string.clockAlreadyAdded,
                        Constans.SNACKBAR_DURATION)
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            }
        }
    }
    
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.clocksList.observe(viewLifecycleOwner) {
            customAdapter.submitList(it)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Utils.setupExceptionHandler()
        //Setting the app name for the action_bar
        _binding = FragmentClockBinding.inflate(inflater)
        initViews()
        checkIsFirstTime()
        
        return binding.root
    }
    
    private fun checkIsFirstTime() {
        //If app opened first time then add one default clock for user.
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (sp.getBoolean(Constans.SP.isClockFirstTime, true)) {
            val edit = sp.edit()
            val clockItem = ClockItem(
                    timeZoneId = TimeZone.getDefault().id,
                    name = Locale.getDefault().displayCountry)
            vm.addItem(clockItem)
            edit.putBoolean(Constans.SP.isClockFirstTime, false)
            edit.apply()
        }
    }
    
    private fun initViews() {
        customAdapter =
            ClockAdapter(this@ClockFragment.requireActivity())
        binding.clockRV.layoutManager = LinearLayoutManager(requireContext())
        
        binding.clockRV.adapter = customAdapter
        
        enableSwipe()
        
        binding.clockAddFab.setOnClickListener {
            countriesResultLauncher.launch(
                    Intent(
                            requireActivity(),
                            CountryActivity::class.java))
        }
        binding.clockAddFab.stateListAnimator = AnimatorInflater.loadStateListAnimator(
                requireContext(),
                R.animator.increase_size_normal_animator)
    }
    
    private fun enableSwipe() {
        //Attached the ItemTouchHelper
        binding.clockRV.addItemDecoration(
                DividerItemDecoration(
                        binding.clockRV.context,
                        DividerItemDecoration.VERTICAL
                )
        )
        
        val ith = CustomItemTouchHelper(
                requireContext(),
                Constans.ITHKey.CLOCK,
                object : CustomItemTouchHelper.OnTouchListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        if (viewHolder is ClockAdapter.ViewHolder) {
                            val position =
                                viewHolder.adapterPosition
                            vm.removeItemAt(position)
                            Utils.handleShowingFAB(binding.clockRV, binding.clockAddFab)
                        }
                    }
                })
        ith.isRoundedCornersEnabled = false
        ItemTouchHelper(ith).attachToRecyclerView(binding.clockRV)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    
}
