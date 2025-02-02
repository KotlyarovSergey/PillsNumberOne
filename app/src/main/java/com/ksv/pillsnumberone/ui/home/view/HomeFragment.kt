package com.ksv.pillsnumberone.ui.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.AppDataBase
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.data.old.FileDataSource
import com.ksv.pillsnumberone.databinding.FragmentHomeBinding
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Interaction
import com.ksv.pillsnumberone.ui.home.model.HomeViewModel
import com.ksv.pillsnumberone.ui.home.model.HomeViewModelFactory
import com.ksv.pillsnumberone.model.PillsService
import com.ksv.pillsnumberone.ui.home.model.HomeState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            PillsService(
                Repository(
                    FileDataSource(requireContext().applicationContext),
                    AppDataBase.getInstance(requireContext().applicationContext).getPillsDao
                )
            )
        )
    }

    private val dataListAdapter = DataListAdapter(
        object : Interaction {
            override fun onRemoveClick(item: DataItem) = viewModel.removeItem(item)
            override fun onUpClick(item: DataItem) = viewModel.moveUp(item)
            override fun onDownClick(item: DataItem) = viewModel.moveDown(item)
            override fun onItemClick(item: DataItem) = viewModel.itemClick(item)
            override fun onItemLongClick(item: DataItem) = viewModel.itemLongClick(item)
            override fun onTimeClick(item: DataItem) = viewModel.onTimeClick(item)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.recycler.adapter = dataListAdapter
        addMenuProvider()
        addLiveDataObservers()
        setFABClickListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addLiveDataObservers() {
        viewModel.dataItems.onEach { data ->
            dataListAdapter.submitList(data)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.state.onEach { state ->
            when (state){
                is HomeState.Normal -> {
                    dataListAdapter.unSellPIlls()
                }
                is HomeState.SelectItem -> {
                    dataListAdapter.setSelected(state.id)
                }
                is HomeState.ModifyItem -> {
                    val action = HomeFragmentDirections.actionMainFragmentToEditDialog(state.id)
                    findNavController().navigate(action)
                }
                is HomeState.AddPills -> {
                    findNavController().navigate(R.id.action_mainFragment_to_editFragment)
                }
                is HomeState.SetTime -> {
                    val pill = state.item as DataItem.Pill
                    val action = HomeFragmentDirections
                        .actionMainFragmentToSetTimeDialog(pill.id, pill.time)
                    findNavController().navigate(action)
                }
                is HomeState.Refresh -> {
                    onMenuResetClick()
                }
                HomeState.Empty -> {
                    // show EmptyHint
                    // realised on DataBinding
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun setFABClickListeners() {
        binding.addButton.setOnClickListener {
            viewModel.onAddClick()
        }
        binding.applyButton.setOnClickListener {
            viewModel.onApplyClick()
        }
    }

    private fun addMenuProvider() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_reset -> {
                        if(viewModel.state.value is HomeState.Normal)
                            viewModel.onRefreshButtonClick()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun onMenuResetClick() {
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.alert_dialog_clear_title))
            .setMessage(getString(R.string.alert_dialog_clear_message))
            .setIcon(R.drawable.icon_refresh_red)
            .setPositiveButton(getString(R.string.alert_dialog_clear_yes)) { _, _ ->
                viewModel.resetPills()
            }
            .setNegativeButton(getString(R.string.alert_dialog_clear_no)) { _, _ -> }
            .setOnDismissListener {
                viewModel.onRefreshDialogDismiss()
            }
        builder.create().show()
    }

}