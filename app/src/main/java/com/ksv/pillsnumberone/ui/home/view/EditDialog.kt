package com.ksv.pillsnumberone.ui.home.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.databinding.DialogEditBinding
import com.ksv.pillsnumberone.ui.home.model.HomeState
import com.ksv.pillsnumberone.ui.home.model.HomeViewModel


class EditDialog : DialogFragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private var id: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(viewModel.state.value){
            is HomeState.ModifyItem -> {
                val stateModify = viewModel.state.value as HomeState.ModifyItem
                viewModel.editDialogIsShown(stateModify.id)
            }
            else -> {
                findNavController().navigate(R.id.action_editDialog_to_homeFragment)
            }
        }

//        if(viewModel.state.value is HomeState.ModifyItem){
//            viewModel.editDialogIsShown((viewModel.state.value as HomeState.ModifyItem).id)
//        } else {
//            findNavController().navigate(R.id.action_editDialog_to_homeFragment)
//        }
//
//        if(viewModel.state.value !is HomeState.ModifyItem){
//            findNavController().navigate(R.id.action_editDialog_to_homeFragment)
//        } else if(viewModel.state.value is HomeState.ModifyItem){
//            viewModel.editDialogIsShown((viewModel.state.value as HomeState.ModifyItem).id)
//        } else {
//            val state = viewModel.state.value
//            throw IllegalArgumentException("Unknown state on EditDialog: $state")
//        }
    }

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
//        id?.let {
//            viewModel.editDialogDismiss(it)
//        }
    }
}