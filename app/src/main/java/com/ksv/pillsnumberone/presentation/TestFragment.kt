package com.ksv.pillsnumberone.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.databinding.FragmentTestBinding
import com.ksv.pillsnumberone.entity.DataItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TestFragment : Fragment() {
    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private val dataListAdapter = DataListAdapter(
        { onMoveUpClick(it) },
        { onMoveDownClick(it) },
        { onRemoveClick(it) }
    )

    private val repo = Repository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(layoutInflater)
        binding.recycler.adapter = dataListAdapter
        //val data = repo.getData()
//        dataListAdapter.data = data

        repo.actualData.onEach {
            dataListAdapter.submitList(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onMoveUpClick(dataItem: DataItem) {
//        Toast.makeText(requireContext(), "MoveUp: ${dataItem.index}", Toast.LENGTH_SHORT).show()
        repo.moveUpItem(dataItem)
    }

    private fun onMoveDownClick(dataItem: DataItem) {
//        Toast.makeText(requireContext(), "MoveDown: ${dataItem.index}", Toast.LENGTH_SHORT).show()
        repo.moveDownItem(dataItem)
    }

    private fun onRemoveClick(dataItem: DataItem) {
//        Toast.makeText(requireContext(), "Remove: ${dataItem.index}", Toast.LENGTH_SHORT).show()
        repo.remove(dataItem)
    }

//    private fun onMoveUpClick(position: Int) {
//        Toast.makeText(requireContext(), "MoveUp: $position", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun onMoveDownClick(position: Int) {
//        Toast.makeText(requireContext(), "MoveDown: $position", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun onRemoveClick(position: Int) {
//        Toast.makeText(requireContext(), "Remove: $position", Toast.LENGTH_SHORT).show()
//        repo.remove(position)
//    }

}