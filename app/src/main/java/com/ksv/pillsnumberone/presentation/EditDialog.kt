package com.ksv.pillsnumberone.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.databinding.DialogEditBinding

class EditDialog(
    private val medicineTitle: String,
    private val medicineRecipe: String,
    private val onOkClick: (String, String) -> Unit,
) : DialogFragment() {
    private var _binding: DialogEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditBinding.inflate(layoutInflater)

        binding.edMedicineTitle.setText(medicineTitle)
        binding.edMedicineRecipe.setText(medicineRecipe)

        binding.edButtonOk.setOnClickListener {
            val title = binding.edMedicineTitle.text.toString()
            val recipe = binding.edMedicineRecipe.text.toString()
                onOkClick(title, recipe)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}