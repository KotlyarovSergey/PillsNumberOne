package com.ksv.pillsnumberone.ui.home.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.databinding.DialogSetTimeBinding
import com.ksv.pillsnumberone.ui.home.model.HomeState
import com.ksv.pillsnumberone.ui.home.model.HomeViewModel
import java.util.Calendar

class SetTimeDialog : DialogFragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: DialogSetTimeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.state.value !is HomeState.SetTime){
            findNavController().navigate(R.id.action_setTimeDialog_to_homeFragment)
        }
        viewModel.setTimeDialogIsShown()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogSetTimeBinding.inflate(requireActivity().layoutInflater)
        val id = SetTimeDialogArgs.fromBundle(requireArguments()).itemId
        val time = SetTimeDialogArgs.fromBundle(requireArguments()).time

        presetTime(time)
        bindDialog(id)
        binding.reset.setOnCheckedChangeListener { _, isChecked ->
            binding.hours.isEnabled = !isChecked
            binding.minutes.isEnabled = !isChecked
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        return alertDialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        //viewModel.setTimeDialogIsShown()
        _binding = null
    }

    private fun bindDialog(itemID: Long) {
        binding.ok.setOnClickListener {
            val time = if (binding.reset.isChecked) {
                null
            } else {
                val hour = binding.hours.value
                val minute = binding.minutes.value
                val zero = if (minute < 10) "0" else ""
                "$hour:$zero$minute"
            }
            viewModel.setTimeFor(itemID, time)
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun presetTime(time: String?) {
        binding.hours.minValue = 0
        binding.hours.maxValue = 23
        binding.minutes.minValue = 0
        binding.minutes.maxValue = 59

        if (time == null) {
            val calendar = Calendar.getInstance()
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            binding.hours.value = hours
            binding.minutes.value = minutes
        } else {
            val splitted = time.split(':')
            if (splitted.size == 2) {
                val hours = splitted[0].toIntOrNull()
                val minutes = splitted[1].toIntOrNull()
                if (hours != null && minutes != null) {
                    binding.hours.value = hours
                    binding.minutes.value = minutes
                }
            }
        }
    }
}