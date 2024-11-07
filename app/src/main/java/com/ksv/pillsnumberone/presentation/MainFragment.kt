package com.ksv.pillsnumberone.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.EatingTime
import com.ksv.pillsnumberone.databinding.FragmentMainBinding
import com.ksv.pillsnumberone.entity.MedicineItem

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DataViewModel by activityViewModels()
    private val breakfastMedicineCardAdapter =
        MedicineCardAdapter(
            { breakfastTimeClick(it) },
            { pos, view -> breakfastMoreClick(pos, view) },
            { breakfastDataChange(it) })
    private val lunchMedicineCardAdapter =
        MedicineCardAdapter(
            { lunchTimeClick(it) },
            { pos, view -> lunchMoreClick(pos, view) },
            { lunchDataChange(it) })
    private val dinnerMedicineCardAdapter =
        MedicineCardAdapter(
            { dinnerTimeClick(it) },
            { pos, view -> dinnerMoreClick(pos, view) },
            { dinnerDataChange(it) })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        addMenuProvider()
        setRecyclerViews()

        binding.addButton.setOnClickListener {
//            viewModel.setAddItemMode()
//            findNavController().navigate(R.id.action_mainFragment_to_editFragment)
            EditDialog { title, recipe -> onEditDialogOkClick(title, recipe) }
                .show(parentFragmentManager, EditDialog::class.java.name)


        }
    }

    private fun onEditDialogOkClick(title: String, recipe: String){
        val text = "$title, $recipe"
        binding.breakfastHeader.text = text
    }

    private fun setRecyclerViews() {
        binding.recyclerBreakfast.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBreakfast.adapter = breakfastMedicineCardAdapter
        breakfastMedicineCardAdapter.setData(viewModel.getBreakfastList())

        binding.recyclerLunch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLunch.adapter = lunchMedicineCardAdapter
        lunchMedicineCardAdapter.setData(viewModel.getLunchList())

        binding.recyclerDinner.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDinner.adapter = dinnerMedicineCardAdapter
        dinnerMedicineCardAdapter.setData(viewModel.getDinnerList())
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
                breakfastMedicineCardAdapter.resetAllItems()
                lunchMedicineCardAdapter.resetAllItems()
                dinnerMedicineCardAdapter.resetAllItems()
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

    private fun breakfastTimeClick(position: Int) {
        onTimeClick(breakfastMedicineCardAdapter, position)
    }

    private fun lunchTimeClick(position: Int) {
        onTimeClick(lunchMedicineCardAdapter, position)
    }

    private fun dinnerTimeClick(position: Int) {
        onTimeClick(dinnerMedicineCardAdapter, position)
    }

    private fun onMoreClick(adapter: MedicineCardAdapter, position: Int, view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popup_move_up -> {
                    adapter.moveUp(position)
                }

                R.id.popup_move_down -> {
                    adapter.moveDown(position)
                }

                R.id.popup_change -> {
                    switchEditMode()
                    val medicine = adapter.getItemAt(position)
                    if (medicine != null) {
                        val time = when (adapter) {
                            breakfastMedicineCardAdapter -> EatingTime.BREAKFAST
                            lunchMedicineCardAdapter -> EatingTime.LUNCH
                            else -> EatingTime.DINNER
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
        viewModel.setEditMode(!viewModel.isEditMode)

        breakfastMedicineCardAdapter.switchEditModeForAll(viewModel.isEditMode)
        lunchMedicineCardAdapter.switchEditModeForAll(viewModel.isEditMode)
        dinnerMedicineCardAdapter.switchEditModeForAll(viewModel.isEditMode)
    }

    private fun breakfastMoreClick(position: Int, view: View) {
        onMoreClick(breakfastMedicineCardAdapter, position, view)
    }

    private fun lunchMoreClick(position: Int, view: View) {
        onMoreClick(lunchMedicineCardAdapter, position, view)
    }

    private fun dinnerMoreClick(position: Int, view: View) {
        onMoreClick(dinnerMedicineCardAdapter, position, view)
    }

    private fun breakfastDataChange(medicineList: List<MedicineItem>) {
//        binding.breakfastHeader.visibility =
        binding.breakfastCard.visibility =
            if (medicineList.isEmpty()) View.GONE
            else View.VISIBLE
        viewModel.saveBreakfastList(medicineList)
    }

    private fun lunchDataChange(medicineList: List<MedicineItem>) {
//        binding.lunchHeader.visibility =
        binding.lunchCard.visibility =
            if (medicineList.isEmpty()) View.GONE
            else View.VISIBLE
        viewModel.saveLunchList(medicineList)
    }

    private fun dinnerDataChange(medicineList: List<MedicineItem>) {
//        binding.dinnerHeader.visibility =
        binding.dinnerCard.visibility =
            if (medicineList.isEmpty()) View.GONE
            else View.VISIBLE
        viewModel.saveDinnerList(medicineList)
    }


}
