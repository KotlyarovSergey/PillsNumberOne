package com.ksv.pillsnumberone.data

import com.ksv.pillsnumberone.entity.MedicineItem

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

    companion object{
        private val morningPills = listOf(
            MedicineItem("Омепразол", "за 30 мин. до еды", time = "6:10", finished = true),
            MedicineItem("Мебиверин", "за 20 мин. до еды", time = "6:20", finished = true),
            MedicineItem("Гастростат", "перед едой", time = "6:40"),
            MedicineItem("Бак-Сет", "во время еды"),
            MedicineItem("Эрмиталь", "во время еды")
        )
        private val noonPills = listOf(
            MedicineItem("Гастростат", "перед едой"),
            MedicineItem("Эрмиталь", "во время еды")
        )
        private val eveningPills = listOf(
            MedicineItem("Омепразол", "за 30 мин. до еды"),
            MedicineItem("Мебиверин", "за 20 мин. до еды"),
            MedicineItem("Гастростат", "перед едой"),
            MedicineItem("Бак-Сет", "во время еды"),
            MedicineItem("Эрмиталь", "во время еды"),
            MedicineItem("Урсодезоксихол", "перед сном")
        )
    }
}