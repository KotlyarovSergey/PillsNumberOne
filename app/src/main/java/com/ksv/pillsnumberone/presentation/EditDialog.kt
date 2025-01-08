package com.ksv.pillsnumberone.presentation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ksv.pillsnumberone.databinding.DialogEditTwoBinding
import com.ksv.pillsnumberone.entity.DataItem


class EditDialog : DialogFragment() {
    private val viewModel: TestViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogEditTwoBinding.inflate(requireActivity().layoutInflater)
        val itemId = EditDialogArgs.fromBundle(requireArguments()).id

        val pill = viewModel.actualData.value.firstOrNull { it is DataItem.Pill && it.id == itemId }
        if (pill != null && pill is DataItem.Pill) {
            binding.edMedicineTitle.setText(pill.title)
            binding.edMedicineRecipe.setText(pill.recipe)
            binding.ok.setOnClickListener {
                val modifiedPill = pill.copy(
                    title = binding.edMedicineTitle.text.toString(),
                    recipe = binding.edMedicineRecipe.text.toString()
                )
                viewModel.modifyPill(modifiedPill)
                dismiss()
            }
            binding.cancel.setOnClickListener { dismiss() }
        } else {
            dismiss()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.finishModify()
    }

}