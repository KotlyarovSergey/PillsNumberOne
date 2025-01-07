package com.ksv.pillsnumberone.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.PillsDataBase
import com.ksv.pillsnumberone.databinding.FragmentTestBinding
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Interaction
import com.ksv.pillsnumberone.model.DataItemService
import com.ksv.pillsnumberone.model.DataItemService2
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TestFragment : Fragment() {
    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TestViewModel by activityViewModels {
        TestViewModelFactory(
            DataItemService(
                PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao
            )
        )
    }
//
//    private val viewModel: TestViewModel2 by activityViewModels {
//        TestViewModelFactory2(
//                PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao
//        )
//    }

    private val dataListAdapter = DataListAdapter(
        object : Interaction {
            override fun onRemoveClick(item: DataItem) = viewModel.removeItem(item)
            override fun onUpClick(item: DataItem) = viewModel.moveUp(item)
            override fun onDownClick(item: DataItem) = viewModel.moveDown(item)
            override fun onItemClick(item: DataItem) = viewModel.itemClick(item)
            override fun onItemLongClick(item: DataItem) = viewModel.itemLongClick(item)
            override fun onTimeClick(item: DataItem) = viewModel.setTimeClick(item)
        }
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(layoutInflater)
        binding.recycler.adapter = dataListAdapter

        binding.testButton.setOnClickListener {
            findNavController().navigate(R.id.action_testFragment_to_editFragment)
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_testFragment_to_editFragment)
        }

        binding.applyButton.setOnClickListener {
            viewModel.finishEditMode()
        }

        viewModel.actualData.onEach { data ->
            dataListAdapter.submitList(data)
            Log.d("ksvlog", "data refresh")
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.setTimeFor.onEach { item ->
            if (item != null) {
                setTime()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isEditMode.onEach { isEdit ->
            //Log.d("ksvlog", "isEdit: $isEdit")
            if (isEdit) {
                binding.applyButton.visibility = View.VISIBLE
                binding.addButton.visibility = View.GONE



            } else {
                binding.applyButton.visibility = View.GONE
                binding.addButton.visibility = View.VISIBLE
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)

//        viewModel.editableItem.onEach { editableItem->
//            editableItem?.let{
//                binding.applyButton.visibility = View.VISIBLE
//                binding.addButton.visibility = View.GONE
//
//            }
//        }.launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setTime() {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(requireActivity().getString(R.string.time_picker_title))
            .build().apply {
                addOnPositiveButtonClickListener {
                    val hour = this.hour
                    var min = this.minute.toString()
                    if (this.minute < 10) min = "0$min"
                    val timeToDisplay = "$hour:$min"
                    viewModel.setTime(timeToDisplay)
                }
                addOnDismissListener {
                    viewModel.setTimeFinished()
                }
            }
        timePicker.show(parentFragmentManager, timePicker::class.java.name)
    }


}