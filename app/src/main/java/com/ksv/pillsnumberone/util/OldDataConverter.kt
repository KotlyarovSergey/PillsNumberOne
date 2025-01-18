package com.ksv.pillsnumberone.util

import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.entity.old.EatingTime
import com.ksv.pillsnumberone.entity.old.MedicineItem

class OldDataConverter {
    fun convert(oldMap: Map<String, List<MedicineItem>>): List<DataItem.Pill> {
        val pills = mutableListOf<DataItem.Pill>()
        oldMap.forEach {
            when (it.key) {
                EatingTime.BREAKFAST.title -> {
                    pills.addAll(medicineListToPillsList(it.value, Period.MORNING))
                }
                EatingTime.LUNCH.title -> {
                    pills.addAll(medicineListToPillsList(it.value, Period.NOON))
                }
                EatingTime.DINNER.title -> {
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
        return pillsList
    }

}