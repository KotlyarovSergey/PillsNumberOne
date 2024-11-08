package com.ksv.pillsnumberone.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.ksv.pillsnumberone.R

class EditDialog(
    private val medicineTitle: String,
    private val medicineRecipe: String,
    private val onOkClick: (String, String) -> Unit,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_edit, null)

        view.findViewById<EditText>(R.id.ed_medicine_title).setText(medicineTitle)
        view.findViewById<EditText>(R.id.ed_medicine_recipe).setText(medicineRecipe)

        val button = view.findViewById<Button>(R.id.ed_button_ok)
        button.setOnClickListener {
            val title = view.findViewById<EditText>(R.id.ed_medicine_title)
            val recipe = view.findViewById<EditText>(R.id.ed_medicine_recipe)
            onOkClick(title.text.toString(), recipe.text.toString())
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}