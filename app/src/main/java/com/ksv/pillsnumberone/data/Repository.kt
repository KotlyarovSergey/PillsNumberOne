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
            MedicineItem("Омепразол", "За 30 мин. до еды"),
            MedicineItem("Мебиверин", "За 20 мин. до еды", time = "6:20"),
            MedicineItem("Гастростат", "Перед едой", finished = true),
            MedicineItem("Бак-Сет", "Во время еды"),
            MedicineItem("Эрмиталь", "Во время еды")
        )
        private val noonPills = listOf(
            MedicineItem("Гастростат", "Перед едой"),
            MedicineItem("Эрмиталь", "Во время еды")
        )
        private val eveningPills = listOf(
            MedicineItem("Омепразол", "За 30 мин. до еды"),
            MedicineItem("Мебиверин", "За 20 мин. до еды"),
            MedicineItem("Гастростат", "Перед едой"),
            MedicineItem("Бак-Сет", "Во время еды"),
            MedicineItem("Эрмиталь", "Во время еды"),
            MedicineItem("Урсодезоксихол", "Перед сном")
        )
    }
}