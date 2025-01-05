package com.ksv.pillsnumberone.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.Pill
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.PillsDataBase
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.databinding.FragmentTestBinding
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Interaction
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.model.DataItemService2
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TestFragment : Fragment() {
    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!
//    private val viewModel: TestViewModel by viewModels()
    private val viewModel: TestViewModel by activityViewModels{
        TestViewModelFactory(
            DataItemService2(
                PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao
            )
        )
    }

    private val dataListAdapter = DataListAdapter(
        object : Interaction{
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
            viewModel.finishEdition()
        }

        viewModel.actualData.onEach {
//            Log.d("ksvlog", "TestFragment viewModel.actualData.onEach\n$it")
            Log.d("ksvlog", "TestFragment фрамент обновился")
            dataListAdapter.submitList(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

//
//        viewModel.data.onEach {
////            Log.d("ksvlog", "TestFragment data.onEach")
////            Log.d("ksvlog", it.toString())
//            dataListAdapter.submitList(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)


//        viewModel.actualData.onEach {
////            Log.d("ksvlog", "TestFragment actualData.onEach")
//            dataListAdapter.submitList(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.setTimeFor.onEach { item ->
            if(item != null) {
                setTime()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isEditMode.onEach { isEdit ->
            if (isEdit){
                binding.applyButton.visibility = View.VISIBLE
                binding.addButton.visibility = View.GONE
            } else {
                binding.applyButton.visibility = View.GONE
                binding.addButton.visibility = View.VISIBLE
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setTime(){
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