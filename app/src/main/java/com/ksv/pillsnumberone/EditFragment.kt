package com.ksv.pillsnumberone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ksv.pillsnumberone.data.Timess
import com.ksv.pillsnumberone.databinding.FragmentEditBinding
import kotlin.math.E

class EditFragment : Fragment() {
    private var _binding : FragmentEditBinding? = null
    private val binding get() = _binding!!
    private var newMedicine = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = EditFragmentArgs.fromBundle(requireArguments()).title
        binding.medicineName.setText(title)

        val recipe = EditFragmentArgs.fromBundle(requireArguments()).recipe
        binding.medicineRecipe.setText(recipe)

        newMedicine = EditFragmentArgs.fromBundle(requireArguments()).newMedicine
        binding.timesCard.visibility = if(newMedicine) View.VISIBLE else View.GONE

        if(!newMedicine) {
            binding.fragmentTitle.text = requireContext().getString(R.string.edit_title_edit)
            val timess = EditFragmentArgs.fromBundle(requireArguments()).times
            when (timess) {
                Timess.MORNING -> binding.checkMorning.isChecked = true
                Timess.NOON -> binding.checkNoon.isChecked = true
                Timess.EVENING -> binding.checkEvening.isChecked = true
            }
        }

        binding.buttonOk.setOnClickListener{
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}