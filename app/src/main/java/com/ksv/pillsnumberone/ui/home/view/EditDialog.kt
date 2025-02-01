package com.ksv.pillsnumberone.ui.home.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ksv.pillsnumberone.databinding.DialogEditBinding
import com.ksv.pillsnumberone.ui.home.model.HomeViewModel
import kotlin.properties.Delegates


class EditDialog : DialogFragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private var id: Long? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogEditBinding.inflate(requireActivity().layoutInflater)
        val itemId = EditDialogArgs.fromBundle(requireArguments()).id
        id = itemId

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        id?.let {
            viewModel.editDialogDismiss(it)
        }
    }
}