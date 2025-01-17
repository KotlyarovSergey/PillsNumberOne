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
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.data.old.FileDataSource
import com.ksv.pillsnumberone.databinding.FragmentAddBinding
import com.ksv.pillsnumberone.model.PillsService
import com.ksv.pillsnumberone.presentation.viewmodel.AddPillViewModel
import com.ksv.pillsnumberone.presentation.viewmodel.AppPIllViewModelProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddPillFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddPillViewModel by viewModels {
        AppPIllViewModelProvider(
            PillsService(
//                PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao,
                Repository(
                    FileDataSource(requireContext().applicationContext),
                    PillsDataBase.getInstance(requireContext().applicationContext).getPillsDao
                )
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}