package com.ksv.pillsnumberone.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.EatingTime
import com.ksv.pillsnumberone.databinding.FragmentAddBinding
import com.ksv.pillsnumberone.entity.MedicineItem

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val dataViewModel: DataViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonOk.setOnClickListener {
            val title = binding.medicineName.text.toString()
            val recipe = binding.medicineRecipe.text.toString()
            val medicine = MedicineItem(title, recipe)

            if (checkFillEatingTime()) {
                if (binding.checkBreakfast.isChecked)
                    dataViewModel.addItem(medicine, EatingTime.BREAKFAST)
                if (binding.checkLunch.isChecked)
                    dataViewModel.addItem(medicine, EatingTime.LUNCH)
                if (binding.checkDinner.isChecked)
                    dataViewModel.addItem(medicine, EatingTime.DINNER)
                findNavController().navigate(R.id.action_editFragment_to_mainFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkFillEatingTime(): Boolean {
        return if (!binding.checkBreakfast.isChecked
            && !binding.checkLunch.isChecked
            && !binding.checkDinner.isChecked
        ) {
            Toast.makeText(
                requireContext(),
                getText(R.string.need_check_message),
                Toast.LENGTH_SHORT
            ).show()
            false
        } else true
    }


}