package com.ksv.pillsnumberone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.data.Timess
import com.ksv.pillsnumberone.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DataViewModel by activityViewModels()
    private val morningMedicineCardAdapter =
        MedicineCardAdapter({ morningTimeClick(it) }, { pos, view -> morningMoreClick(pos, view) },
            { morningDataChange() })
    private val noonMedicineCardAdapter =
        MedicineCardAdapter(
            { noonTimeClick(it) },
            { pos, view -> noonMoreClick(pos, view) },
            { noonDataChange() })
    private val eveningMedicineCardAdapter =
        MedicineCardAdapter(
            { eveningTimeClick(it) },
            { pos, view -> eveningMoreClick(pos, view) },
            { eveningDataChange() })
    //private var edition = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //edition = viewModel.isEditMode
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMenuProvider()
        setRecyclerViews()

        binding.eveningTitle.setOnClickListener {
//            morningMedicineCardAdapter.notifySetChange()
            viewModel.setAddItemMode()
            findNavController().navigate(R.id.action_mainFragment_to_editFragment)

//            val medicineList = eveningMedicineCardAdapter.getItems()
//            Log.d("ksvlog", medicineList.toString())
        }
    }

    private fun setRecyclerViews() {
        binding.recyclerMorning.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMorning.adapter = morningMedicineCardAdapter
        //morningMedicineCardAdapter.setData(morningPills)
        morningMedicineCardAdapter.setData(viewModel.getMorningList())

        binding.recyclerNoon.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNoon.adapter = noonMedicineCardAdapter
//        noonMedicineCardAdapter.setData(noonPills)
        noonMedicineCardAdapter.setData(viewModel.getNoonList())

        binding.recyclerEvening.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEvening.adapter = eveningMedicineCardAdapter
//        eveningMedicineCardAdapter.setData(eveningPills)
        eveningMedicineCardAdapter.setData(viewModel.getEveningList())
    }

    private fun addMenuProvider() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                menu.findItem(R.id.menu_edit).setIcon(
                    if (viewModel.isEditMode) R.drawable.baseline_check_24
                    else R.drawable.baseline_edit_off
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_clear -> {
                        if (!viewModel.isEditMode) menuClearClick()
                        true
                    }

                    R.id.menu_edit -> {
//                        edition = !edition
                        //viewModel.isEditMode = edition
//                        morningMedicineCardAdapter.switchEditModeForAll(edition)
//                        noonMedicineCardAdapter.switchEditModeForAll(edition)
//                        eveningMedicineCardAdapter.switchEditModeForAll(edition)
                        switchEditMode()
                        if (viewModel.isEditMode) menuItem.setIcon(R.drawable.baseline_check_24)
                        else menuItem.setIcon(R.drawable.baseline_edit_off)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun menuClearClick() {
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.alert_dialog_clear_title))
            .setMessage(getString(R.string.alert_dialog_clear_message))
            .setIcon(R.drawable.baseline_cached_red_24)
            .setPositiveButton(getString(R.string.alert_dialog_clear_yes)) { _, _ ->
                morningMedicineCardAdapter.resetAllItems()
                noonMedicineCardAdapter.resetAllItems()
                eveningMedicineCardAdapter.resetAllItems()
            }
            .setNegativeButton(getString(R.string.alert_dialog_clear_no)) { _, _ -> }
        builder.create().show()
    }

    private fun onTimeClick(adapter: MedicineCardAdapter, position: Int) {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(requireActivity().getString(R.string.time_picker_title))
            .build().apply {
                addOnPositiveButtonClickListener {
                    val hour = this.hour
                    var min = this.minute.toString()
                    if (this.minute < 10) min = "0$min"
                    val timeToDisplay = "$hour:$min"
                    adapter.setTimeAt(position, timeToDisplay)
                }
            }
        timePicker.show(parentFragmentManager, timePicker::class.java.name)
    }

    private fun morningTimeClick(position: Int) {
        onTimeClick(morningMedicineCardAdapter, position)
    }

    private fun noonTimeClick(position: Int) {
        onTimeClick(noonMedicineCardAdapter, position)
    }

    private fun eveningTimeClick(position: Int) {
        onTimeClick(eveningMedicineCardAdapter, position)
    }

    private fun onMoreClick(adapter: MedicineCardAdapter, position: Int, view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popup_move_up -> adapter.moveUp(position)
                R.id.popup_move_down -> adapter.moveDown(position)
                R.id.popup_change -> {
                    switchEditMode()
                    val medicine = adapter.getItemAt(position)
                    if (medicine != null) {
//                        val timess = when(adapter){
//                            morningMedicineCardAdapter -> Timess.MORNING
//                            noonMedicineCardAdapter -> Timess.NOON
//                            else -> Timess.EVENING
//                        }
//                        val action = MainFragmentDirections
//                            .actionMainFragmentToEditFragment(
//                                title = medicine.title, recipe = medicine.recipe, times = timess, newMedicine = false
//                            )
//                        findNavController().navigate(action)

                        val time = when(adapter){
                            morningMedicineCardAdapter -> Timess.MORNING
                            noonMedicineCardAdapter -> Timess.NOON
                            else -> Timess.EVENING
                        }
                        viewModel.setEditItemMode(position, time)


                        findNavController().navigate(R.id.action_mainFragment_to_editFragment)
                    }


                }

                R.id.popup_remove -> {
                    adapter.removeItemAt(position)
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun switchEditMode() {
//        edition = !edition
        viewModel.setEditMode(!viewModel.isEditMode)
        morningMedicineCardAdapter.switchEditModeForAll(viewModel.isEditMode)
        noonMedicineCardAdapter.switchEditModeForAll(viewModel.isEditMode)
        eveningMedicineCardAdapter.switchEditModeForAll(viewModel.isEditMode)
    }

    private fun morningMoreClick(position: Int, view: View) {
        onMoreClick(morningMedicineCardAdapter, position, view)
    }

    private fun noonMoreClick(position: Int, view: View) {
        onMoreClick(noonMedicineCardAdapter, position, view)
    }

    private fun eveningMoreClick(position: Int, view: View) {
        onMoreClick(eveningMedicineCardAdapter, position, view)
    }

    private fun morningDataChange() {
        viewModel.saveMorningList(morningMedicineCardAdapter.getAllItems())
    }

    private fun noonDataChange() {
        viewModel.saveNoonList(noonMedicineCardAdapter.getAllItems())
    }

    private fun eveningDataChange() {
        viewModel.saveEveningList(eveningMedicineCardAdapter.getAllItems())
    }


}
