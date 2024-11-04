package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.data.State
import com.ksv.pillsnumberone.data.EatingTime
import com.ksv.pillsnumberone.entity.MedicineItem

class DataViewModel : ViewModel() {
    private var _breakfastMedicineList: MutableList<MedicineItem>
    private var _lunchMedicineList: MutableList<MedicineItem>
    private var _dinnerMedicineList: MutableList<MedicineItem>
    private var _isEditMode = false
    val isEditMode get() = _isEditMode
    private var _state: State = State.Normal
    val state get() = _state
    private var _editableMedicine = MedicineItem("", "")
    val editableMedicine get() = _editableMedicine

    init {
        val repo = Repository()
        val allData = repo.load()
        _breakfastMedicineList = (allData[EatingTime.BREAKFAST.title] ?: emptyList()).toMutableList()
        _lunchMedicineList = (allData[EatingTime.LUNCH.title] ?: emptyList()).toMutableList()
        _dinnerMedicineList = (allData[EatingTime.DINNER.title] ?: emptyList()).toMutableList()

        _breakfastMedicineList.forEach { it.editable = false }
        _lunchMedicineList.forEach { it.editable = false }
        _dinnerMedicineList.forEach { it.editable = false }
    }

    fun saveBreakfastList(medicineList: List<MedicineItem>) {
        _breakfastMedicineList = medicineList.toMutableList()
        saveData()
    }

    fun saveLunchList(medicineList: List<MedicineItem>) {
        _lunchMedicineList = medicineList.toMutableList()
        saveData()
    }

    fun saveDinnerList(medicineList: List<MedicineItem>) {
        _dinnerMedicineList = medicineList.toMutableList()
        saveData()
    }

    private fun saveData() {
        viewModelScope.let {
            val repo = Repository()
            repo.save(
                mapOf(
                    EatingTime.BREAKFAST.title to _breakfastMedicineList,
                    EatingTime.LUNCH.title to _lunchMedicineList,
                    EatingTime.DINNER.title to _dinnerMedicineList
                )
            )
        }
    }

    fun getBreakfastList(): List<MedicineItem> = _breakfastMedicineList
    fun getLunchList(): List<MedicineItem> = _lunchMedicineList
    fun getDinnerList(): List<MedicineItem> = _dinnerMedicineList

    fun setEditMode(isEdit: Boolean) {
        _isEditMode = isEdit
    }

    fun setAddItemMode() {
        _state = State.AddNewItem
    }

    fun addItem(item: MedicineItem, time: EatingTime) {
        when (time) {
            EatingTime.BREAKFAST -> _breakfastMedicineList.add(item)
            EatingTime.LUNCH -> _lunchMedicineList.add(item)
            EatingTime.DINNER -> _dinnerMedicineList.add(item)
        }
        saveData()
        _state = State.Normal
    }

    fun setEditItemMode(index: Int, eatingTime: EatingTime) {
        _editableMedicine = when (eatingTime) {
            EatingTime.BREAKFAST -> _breakfastMedicineList[index]
            EatingTime.LUNCH -> _lunchMedicineList[index]
            EatingTime.DINNER -> _dinnerMedicineList[index]
        }
        _state = State.EditItem(index, eatingTime)
    }

    fun editItem(item: MedicineItem) {
        val timess = _state.eatingTime!!
        val index = _state.index!!
        when (timess) {
            EatingTime.BREAKFAST -> _breakfastMedicineList[index] = item
            EatingTime.LUNCH -> _lunchMedicineList[index] = item
            EatingTime.DINNER -> _dinnerMedicineList[index] = item
        }
        saveData()
        _state = State.Normal
    }


}