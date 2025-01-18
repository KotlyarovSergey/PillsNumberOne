package com.ksv.pillsnumberone.util

import android.util.Log
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.entity.old.EatingTime
import com.ksv.pillsnumberone.entity.old.MedicineItem

class OldDataConverter {
    fun convert(oldMap: Map<String, List<MedicineItem>>): List<DataItem.Pill> {
        Log.d("ksvlog", "oldMap: $oldMap")
        val pills = mutableListOf<DataItem.Pill>()
        oldMap.forEach {
            when (it.key) {
                EatingTime.BREAKFAST.title -> {
                    Log.d("ksvlog", "BREAKFAST:")
                    pills.addAll(medicineListToPillsList(it.value, Period.MORNING))
                }
                EatingTime.LUNCH.title -> {
                    Log.d("ksvlog", "LUNCH:")
                    pills.addAll(medicineListToPillsList(it.value, Period.NOON))
                }
                EatingTime.DINNER.title -> {
                    Log.d("ksvlog", "DINNER:")
                    pills.addAll(medicineListToPillsList(it.value, Period.EVENING))
                }
            }
        }

        return pills
    }

    private fun medicineToPill(medicine: MedicineItem, period: Period): DataItem.Pill {
        return DataItem.Pill(
            title = medicine.title,
            recipe = medicine.recipe,
            finished = medicine.finished,
            time = medicine.time,
            period = period
        )
    }

    private fun medicineListToPillsList(medicineList: List<MedicineItem>, period: Period): List<DataItem.Pill>{
        val pillsList = mutableListOf<DataItem.Pill>()
        medicineList.forEachIndexed { index, medicineItem ->
            pillsList.add(
                medicineToPill(medicineItem, period).copy(position = index)
            )
        }
        Log.d("ksvlog", pillsList.toString())
        return pillsList
    }

}