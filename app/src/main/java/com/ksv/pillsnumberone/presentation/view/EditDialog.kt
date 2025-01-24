package com.ksv.pillsnumberone.presentation.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ksv.pillsnumberone.databinding.DialogEditBinding
import com.ksv.pillsnumberone.presentation.viewmodel.DataViewModel


class EditDialog : DialogFragment() {
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogEditBinding.inflate(requireActivity().layoutInflater)
        val itemId = EditDialogArgs.fromBundle(requireArguments()).id

        val pill = viewModel.getPillByID(itemId)
        pill?.let {
            binding.edMedicineTitle.setText(pill.title)
            binding.edMedicineRecipe.setText(pill.recipe)
            binding.ok.setOnClickListener {
                val modifiedPill = pill.copy(
                    title = binding.edMedicineTitle.text.toString().trim(),
                    recipe = binding.edMedicineRecipe.text.toString().trim()
                )
                viewModel.modifyPill(modifiedPill)
                dismiss()
            }
            binding.cancel.setOnClickListener { dismiss() }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        return dialog
    }
}