package com.ksv.pillsnumberone.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.data.EatingTime
import com.ksv.pillsnumberone.entity.MedicineItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DataViewModel : ViewModel() {
    private var _breakfastMedicineList: MutableList<MedicineItem>
    private var _lunchMedicineList: MutableList<MedicineItem>
    private var _dinnerMedicineList: MutableList<MedicineItem>
    private var _editableTime: EatingTime? = null
    val editableTime get() = _editableTime
    private val _emptyAllLists = MutableStateFlow(false)
    val emptyAllLists = _emptyAllLists.asStateFlow()

    init {
        val repo = Repository()
        val allData = repo.load()
        _breakfastMedicineList =
            (allData[EatingTime.BREAKFAST.title] ?: emptyList()).toMutableList()
        _lunchMedicineList = (allData[EatingTime.LUNCH.title] ?: emptyList()).toMutableList()
        _dinnerMedicineList = (allData[EatingTime.DINNER.title] ?: emptyList()).toMutableList()

        _breakfastMedicineList.forEach { it.editable = false }
        _lunchMedicineList.forEach { it.editable = false }
        _dinnerMedicineList.forEach { it.editable = false }

        checkEmptyLists()
    }

    private fun checkEmptyLists() {
        _emptyAllLists.value =
            _breakfastMedicineList.isEmpty() &&
            _lunchMedicineList.isEmpty() &&
            _dinnerMedicineList.isEmpty()

    }

    fun saveBreakfastList(medicineList: List<MedicineItem>) {
        _breakfastMedicineList = medicineList.toMutableList()
        checkEmptyLists()
        saveData()
    }

    fun saveLunchList(medicineList: List<MedicineItem>) {
        _lunchMedicineList = medicineList.toMutableList()
        checkEmptyLists()
        saveData()
    }

    fun saveDinnerList(medicineList: List<MedicineItem>) {
        _dinnerMedicineList = medicineList.toMutableList()
        checkEmptyLists()
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

    fun clearPermissionOnEdit() {
        _editableTime = null
    }

    fun setPermissionOnEditTo(eatingTime: EatingTime) {
        _editableTime = eatingTime
    }

    fun addItem(item: MedicineItem, time: EatingTime) {
        when (time) {
            EatingTime.BREAKFAST -> _breakfastMedicineList.add(item)
            EatingTime.LUNCH -> _lunchMedicineList.add(item)
            EatingTime.DINNER -> _dinnerMedicineList.add(item)
        }
        saveData()
        checkEmptyLists()
    }

}