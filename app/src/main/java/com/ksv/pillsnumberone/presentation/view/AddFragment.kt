package com.ksv.pillsnumberone.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.PillsDataBase
import com.ksv.pillsnumberone.databinding.FragmentAddBinding
import com.ksv.pillsnumberone.model.DataItemService
import com.ksv.pillsnumberone.presentation.viewmodel.AddPillViewModel
import com.ksv.pillsnumberone.presentation.viewmodel.AppPIllViewModelProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    //    private val dataViewModel: DataViewModel by activityViewModels()
//    private val testViewModel: TestViewModel by activityViewModels()
//    private val testViewModel: TestViewModel2 by activityViewModels()
    private val viewModel: AddPillViewModel by viewModels {
        AppPIllViewModelProvider(
            DataItemService(
                PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAdd.setOnClickListener {
            viewModel.morningCheck = binding.checkBreakfast.isChecked
            viewModel.noonCheck = binding.checkLunch.isChecked
            viewModel.eveningCheck = binding.checkDinner.isChecked
            viewModel.title = binding.medicineName.text.toString()
            viewModel.recipe = binding.medicineRecipe.text.toString()
            viewModel.onAddClick()

//            val title = binding.medicineName.text.toString()
//            val recipe = binding.medicineRecipe.text.toString()
//            //val medicine = MedicineItem(title, recipe)
//
//            if (checkFillEatingTime()) {
//                if (binding.checkBreakfast.isChecked)
//                    testViewModel.addItem(DataItem.Pill(title = title, recipe = recipe, period = Period.MORNING))
//                if (binding.checkLunch.isChecked)
//                    testViewModel.addItem(DataItem.Pill(title = title, recipe = recipe, period = Period.NOON))
//                if (binding.checkDinner.isChecked)
//                    testViewModel.addItem(DataItem.Pill(title = title, recipe = recipe, period = Period.EVENING))
//                findNavController().navigate(R.id.action_editFragment_to_testFragment)
//            }
        }

        viewModel.backToMainFragment.onEach { goBack ->
            if (goBack)
                findNavController().navigate(R.id.action_editFragment_to_testFragment)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.errorNotChecked.onEach { isError ->
            if (isError) {
                Toast.makeText(
                    requireContext(),
                    getText(R.string.need_check_message),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.errorWasMessaged()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun checkFillEatingTime(): Boolean {
//        return if (!binding.checkBreakfast.isChecked
//            && !binding.checkLunch.isChecked
//            && !binding.checkDinner.isChecked
//        ) {
//            Toast.makeText(
//                requireContext(),
//                getText(R.string.need_check_message),
//                Toast.LENGTH_SHORT
//            ).show()
//            false
//        } else true
//    }


}