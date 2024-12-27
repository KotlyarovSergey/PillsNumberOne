package com.ksv.pillsnumberone.data

import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.MedicineItem
import com.ksv.pillsnumberone.entity.Period

class Repository {

    fun save(allMedicine: Map<String, List<MedicineItem>>){
        val fds = FileDataSource()
        fds.saveData(allMedicine)
    }

    fun load(): Map<String, List<MedicineItem>>{
        val fds = FileDataSource()
        val map = fds.loadData()
        return map
//        return mapOf(
//            EatingTime.BREAKFAST.title to morningPills,
//            EatingTime.LUNCH.title to noonPills,
//            EatingTime.DINNER.title to eveningPills)
    }

    fun getData(): List<DataItem> = DEFAULT_DATA_ITEMS

    companion object{
        private val DEFAULT_DATA_ITEMS = listOf(
            DataItem.Caption("Утро", Period.MORNING),
            DataItem.Pill("Омепразол", "за 30 мин. до еды", Period.MORNING, 0, "6:10", true),
            DataItem.Pill("Мебиверин", "за 20 мин. до еды", Period.MORNING, 1, "6:20", true),
            DataItem.Pill("Гастростат", "перед едой", Period.MORNING, 2, time = "6:40"),
            DataItem.Pill("Бак-Сет", "во время еды", Period.MORNING, 3),
            DataItem.Pill("Эрмиталь", "во время еды", Period.MORNING, 4),

            DataItem.Caption("Обед", Period.NOON),
            DataItem.Pill("Гастростат", "перед едой", Period.NOON, 0),
            DataItem.Pill("Эрмиталь", "во время еды", Period.NOON, 1),

            DataItem.Caption("Вечер", Period.EVENING),
            DataItem.Pill("Омепразол", "за 30 мин. до еды", Period.EVENING, 0),
            DataItem.Pill("Мебиверин", "за 20 мин. до еды", Period.EVENING, 1),
            DataItem.Pill("Гастростат", "перед едой", Period.EVENING, 2),
            DataItem.Pill("Бак-Сет", "во время еды", Period.EVENING, 3),
            DataItem.Pill("Эрмиталь", "во время еды", Period.EVENING, 4),
            DataItem.Pill("Урсодезоксихол", "перед сном", Period.EVENING, 5)

        )
    }
}