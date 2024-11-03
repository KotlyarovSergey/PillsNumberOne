package com.ksv.pillsnumberone

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.data.State
import com.ksv.pillsnumberone.data.Times
import com.ksv.pillsnumberone.data.Timess
import com.ksv.pillsnumberone.entity.MedicineItem

class DataViewModel : ViewModel() {
    private var _morningMedicineList: MutableList<MedicineItem>
    private var _noonMedicineList: MutableList<MedicineItem>
    private var _eveningMedicineList: MutableList<MedicineItem>
    private var _isEditMode = false
    val isEditMode get() = _isEditMode
    private var _state: State = State.Normal
    val state get() = _state
    private var _editableMedicine = MedicineItem("", "")
    val editableMedicine get() = _editableMedicine

    init {
        Log.d("ksvlog", "VM init")
        val repo = Repository()
        val allData = repo.load()
        _morningMedicineList = (allData[Times.MORNING] ?: emptyList()).toMutableList()
        _noonMedicineList = (allData[Times.NOON] ?: emptyList()).toMutableList()
        _eveningMedicineList = (allData[Times.EVENING] ?: emptyList()).toMutableList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ksvlog", "VM cleared")
        val repo = Repository()
        repo.save(mapOf(
            Times.MORNING to _morningMedicineList,
            Times.NOON to _eveningMedicineList,
            Times.EVENING to _eveningMedicineList
        ))
    }

    fun saveMorningList(medicineList: List<MedicineItem>) {
        _morningMedicineList = medicineList.toMutableList()
    }

    fun saveNoonList(medicineList: List<MedicineItem>) {
        _noonMedicineList = medicineList.toMutableList()
    }

    fun saveEveningList(medicineList: List<MedicineItem>) {
        _eveningMedicineList = medicineList.toMutableList()
    }

    fun getMorningList(): List<MedicineItem> = _morningMedicineList
    fun getNoonList(): List<MedicineItem> = _noonMedicineList
    fun getEveningList(): List<MedicineItem> = _eveningMedicineList

    fun setEditMode(isEdit: Boolean) {
        _isEditMode = isEdit
    }

    fun setAddItemMode() {
        _state = State.AddNewItem
    }

    fun addItem(item: MedicineItem, time: Timess) {
        when (time) {
            Timess.MORNING -> _morningMedicineList.add(item)
            Timess.NOON -> _noonMedicineList.add(item)
            Timess.EVENING -> _eveningMedicineList.add(item)
        }
        _state = State.Normal
    }

    fun setEditItemMode(index: Int, timess: Timess) {
        _editableMedicine = when (timess) {
            Timess.MORNING -> _morningMedicineList[index]
            Timess.NOON -> _noonMedicineList[index]
            Timess.EVENING -> _eveningMedicineList[index]
        }
        _state = State.EditItem(index, timess)
    }

//    fun editItem(index: Int, item: MedicineItem, timess: Timess) {
//        when (timess) {
//            Timess.MORNING -> _morningMedicineList[index] = item
//            Timess.NOON -> _noonMedicineList[index] = item
//            Timess.EVENING -> _eveningMedicineList[index] = item
//        }
//        _state = State.Normal
//    }

    fun editItem(item: MedicineItem) {
        val timess = _state.timess!!
        val index = _state.index!!
        when (timess) {
            Timess.MORNING -> _morningMedicineList[index] = item
            Timess.NOON -> _noonMedicineList[index] = item
            Timess.EVENING -> _eveningMedicineList[index] = item
        }
        _state = State.Normal
    }
}