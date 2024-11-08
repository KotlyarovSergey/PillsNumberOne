package com.ksv.pillsnumberone.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
            { onBreakfastItemLongClick() },
            { breakfastItemClick(it) },
            { breakfastDataChange(it) })
    private val lunchMedicineCardAdapter =
        MedicineCardAdapter(
            { lunchTimeClick(it) },
            { onLunchItemLongClick() },
            { lunchItemClick(it) },
            { lunchDataChange(it) })
    private val dinnerMedicineCardAdapter =
        MedicineCardAdapter(
            { dinnerTimeClick(it) },
            { onDinnerItemLongClick() },
            { dinnerItemClick(it) },
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
        setEditAndAddButtons()

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editFragment)
        }

        binding.applyButton.setOnClickListener {
            breakfastMedicineCardAdapter.finishEdition()
            lunchMedicineCardAdapter.finishEdition()
            dinnerMedicineCardAdapter.finishEdition()
            viewModel.setEditMode(false)
            setEditAndAddButtons()
        }
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

        when(viewModel.editableTime){
            EatingTime.BREAKFAST -> breakfastMedicineCardAdapter.denyPermissionToEdit()
            EatingTime.LUNCH -> lunchMedicineCardAdapter.denyPermissionToEdit()
            EatingTime.DINNER -> dinnerMedicineCardAdapter.denyPermissionToEdit()
            null -> {}
        }
    }

    private fun addMenuProvider() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_clear -> {
                        if (!viewModel.isEditMode) menuClearClick()
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

    private fun onItemLongClick() {
        viewModel.setEditMode(true)
        setEditAndAddButtons()
    }

    private fun onBreakfastItemLongClick(){
        viewModel.setPermissionOnEditTo(EatingTime.BREAKFAST)
        lunchMedicineCardAdapter.denyPermissionToEdit()
        dinnerMedicineCardAdapter.denyPermissionToEdit()
        onItemLongClick()
    }

    private fun onLunchItemLongClick(){
        viewModel.setPermissionOnEditTo(EatingTime.LUNCH)
        breakfastMedicineCardAdapter.denyPermissionToEdit()
        dinnerMedicineCardAdapter.denyPermissionToEdit()
        onItemLongClick()
    }
    private fun onDinnerItemLongClick(){
        viewModel.setPermissionOnEditTo(EatingTime.DINNER)
        breakfastMedicineCardAdapter.denyPermissionToEdit()
        lunchMedicineCardAdapter.denyPermissionToEdit()
        onItemLongClick()
    }


    private fun setEditAndAddButtons() {
        val isEdit = viewModel.isEditMode
        binding.applyButton.visibility = if (isEdit) View.VISIBLE else View.GONE
        binding.addButton.visibility = if (isEdit) View.GONE else View.VISIBLE
    }

    private fun onItemClick(adapter: MedicineCardAdapter, position: Int) {
        val oldMedicineItem = adapter.getItemAt(position) ?: MedicineItem("", "")

        val view = layoutInflater.inflate(R.layout.dialog_edit, null)
        view.findViewById<EditText>(R.id.ed_medicine_title).setText(oldMedicineItem.title)
        view.findViewById<EditText>(R.id.ed_medicine_recipe).setText(oldMedicineItem.recipe)

        AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton(getString(R.string.button_ok)){ _, _ ->
                val title = view.findViewById<EditText>(R.id.ed_medicine_title).text.toString()
                val recipe = view.findViewById<EditText>(R.id.ed_medicine_recipe).text.toString()
                val newMedicineItem =
                    MedicineItem(
                        title,
                        recipe,
                        oldMedicineItem.finished,
                        oldMedicineItem.time,
                        editable = true
                    )
                adapter.updateItemAt(position, newMedicineItem)
            }
            .create().show()
    }

    private fun breakfastItemClick(position: Int) {
        onItemClick(breakfastMedicineCardAdapter, position)
    }

    private fun lunchItemClick(position: Int) {
        onItemClick(lunchMedicineCardAdapter, position)
    }

    private fun dinnerItemClick(position: Int) {
        onItemClick(dinnerMedicineCardAdapter, position)
    }

    private fun breakfastDataChange(medicineList: List<MedicineItem>) {
        binding.breakfastCard.visibility =
            if (medicineList.isEmpty()) View.GONE
            else View.VISIBLE
        viewModel.saveBreakfastList(medicineList)
    }

    private fun lunchDataChange(medicineList: List<MedicineItem>) {
        binding.lunchCard.visibility =
            if (medicineList.isEmpty()) View.GONE
            else View.VISIBLE
        viewModel.saveLunchList(medicineList)
    }

    private fun dinnerDataChange(medicineList: List<MedicineItem>) {
        binding.dinnerCard.visibility =
            if (medicineList.isEmpty()) View.GONE
            else View.VISIBLE
        viewModel.saveDinnerList(medicineList)
    }


}
