package com.ksv.pillsnumberone.presentation.view

import android.os.Bundle
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
import com.ksv.pillsnumberone.data.PillsDataBase
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.data.old.FileDataSource
import com.ksv.pillsnumberone.databinding.FragmentMainBinding
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Interaction
import com.ksv.pillsnumberone.model.PillsService
import com.ksv.pillsnumberone.presentation.viewmodel.DataViewModel
import com.ksv.pillsnumberone.presentation.viewmodel.DataViewModelFactory
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DataViewModel by activityViewModels {
        DataViewModelFactory(
            PillsService(
//                PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao,
                Repository(
                    FileDataSource(requireContext().applicationContext),
                    PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao
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
        _binding = FragmentMainBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.recycler.adapter = dataListAdapter
        addMenuProvider()
        addLiveDataObservers()
        addButtonClickListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addLiveDataObservers() {
        viewModel.actualData.onEach { data ->
            dataListAdapter.submitList(data)
//            Log.d("ksvlog", "data refresh")
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.setTimeFor.onEach { item ->
            if (item != null) {
                val action = MainFragmentDirections
                    .actionTestFragmentToSetTimeDialog(item.id, item.time)
                findNavController().navigate(action)
                viewModel.setTimeFinished()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isEditMode.onEach { isEdit ->
            if (isEdit) {
                binding.applyButton.visibility = View.VISIBLE
                binding.addButton.visibility = View.GONE
            } else {
                binding.applyButton.visibility = View.GONE
                binding.addButton.visibility = View.VISIBLE
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.modifiedItem.onEach { modifiedItem ->
            modifiedItem?.let {
                if (modifiedItem is DataItem.Pill) {
                    val action = MainFragmentDirections
                        .actionTestFragmentToEditDialog(modifiedItem.id)
                    findNavController().navigate(action)
                    viewModel.resetModifiedItem()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.emptyDataHint.onEach { showHint ->
            showHideHint(showHint)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun addButtonClickListeners() {
        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_testFragment_to_editFragment)
        }
        binding.applyButton.setOnClickListener {
            viewModel.finishEditMode()
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
                    R.id.menu_clear -> {
                        if (!viewModel.isEditMode.value && !viewModel.emptyDataHint.value)
                            onMenuClearClick()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun onMenuClearClick() {
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.alert_dialog_clear_title))
            .setMessage(getString(R.string.alert_dialog_clear_message))
            .setIcon(R.drawable.icon_refresh_red)
            .setPositiveButton(getString(R.string.alert_dialog_clear_yes)) { _, _ ->
                viewModel.resetPills()
            }
            .setNegativeButton(getString(R.string.alert_dialog_clear_no)) { _, _ -> }
        builder.create().show()
    }

    private fun showHideHint(show: Boolean){
        if (show) {
            binding.arrow.visibility = View.VISIBLE
            binding.emptyListText.visibility = View.VISIBLE
            binding.clickPlusText.visibility = View.VISIBLE
        } else {
            binding.arrow.visibility = View.GONE
            binding.emptyListText.visibility = View.GONE
            binding.clickPlusText.visibility = View.GONE
        }
    }

}