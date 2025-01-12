package com.ksv.pillsnumberone.presentation.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ksv.pillsnumberone.databinding.DialogSetTimeBinding
import com.ksv.pillsnumberone.presentation.viewmodel.DataViewModel
import java.util.Calendar

class SetTimeDialog : DialogFragment() {
    private val viewModel: DataViewModel by activityViewModels()
    private var _binding: DialogSetTimeBinding? = null
    private val binding get() = _binding!!

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

//        val dialog = Dialog(requireContext())
//        dialog.setContentView(binding.root)
//        dialog.create()
//        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
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