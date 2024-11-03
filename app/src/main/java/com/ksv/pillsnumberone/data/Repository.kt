package com.ksv.pillsnumberone.data

import com.ksv.pillsnumberone.entity.MedicineItem

class Repository {

    fun save(data: Map<String, List<MedicineItem>>){

    }

    fun load(): Map<String, List<MedicineItem>>{
        return mapOf(
            Times.MORNING to morningPills,
            Times.NOON to noonPills,
            Times.EVENING to eveningPills)
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