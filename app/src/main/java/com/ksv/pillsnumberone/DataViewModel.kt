package com.ksv.pillsnumberone

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.data.TIMES
import com.ksv.pillsnumberone.entity.MedicineItem

class DataViewModel: ViewModel() {
    private var _morningMedicineList: List<MedicineItem> = emptyList()
    private var _noonMedicineList: List<MedicineItem> = emptyList()
    private var _eveningMedicineList: List<MedicineItem> = emptyList()

    init {
        Log.d("ksvlog", "VM init")
        val repo = Repository()
        val allData = repo.load()
        _morningMedicineList = allData[TIMES.MORNING] ?: emptyList()
        _noonMedicineList = allData[TIMES.NOON] ?: emptyList()
        _eveningMedicineList = allData[TIMES.EVENING] ?: emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ksvlog", "VM cleared")
    }

    fun saveMorningList(medicineList: List<MedicineItem>){
        _morningMedicineList = medicineList
    }
    fun saveNoonList(medicineList: List<MedicineItem>){
        _noonMedicineList = medicineList
    }
    fun saveEveningList(medicineList: List<MedicineItem>){
        _eveningMedicineList = medicineList
    }

    fun getMorningList(): List<MedicineItem> = _morningMedicineList
    fun getNoonList(): List<MedicineItem> = _noonMedicineList
    fun getEveningList(): List<MedicineItem> = _eveningMedicineList

}