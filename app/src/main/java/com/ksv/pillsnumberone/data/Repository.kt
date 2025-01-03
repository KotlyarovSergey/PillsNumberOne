package com.ksv.pillsnumberone.data

import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.MedicineItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Collections

class Repository {
//    private val _actualData = MutableStateFlow<List<DataItem>>(emptyList())
//    val actualData = _actualData.asStateFlow()

    private var actualData: MutableList<DataItem> = mutableListOf()
    init {
//        _actualData.value = DEFAULT_DATA_ITEMS
        actualData = DEFAULT_DATA_ITEMS.toMutableList()
    }


    fun save(allMedicine: Map<String, List<MedicineItem>>) {
        val fds = FileDataSource()
        fds.saveData(allMedicine)
    }

    fun load(): Map<String, List<MedicineItem>> {
        val fds = FileDataSource()
        val map = fds.loadData()
        return map
//        return mapOf(
//            EatingTime.BREAKFAST.title to morningPills,
//            EatingTime.LUNCH.title to noonPills,
//            EatingTime.DINNER.title to eveningPills)
    }

    fun getData(): List<DataItem> = DEFAULT_DATA_ITEMS
    fun getPills(): List<DataItem.Pill> = DEFAULT_PILLS_LIST2


    companion object {
        private val DEFAULT_PILLS_LIST2 = listOf(
            DataItem.Pill(14, "Бак-Сет", "во время еды", Period.MORNING, 3),
            DataItem.Pill(12, "Омепразол", "за 30 мин. до еды", Period.MORNING, 0, "6:10", true),
            DataItem.Pill(32, "Гастростат", "перед едой", Period.MORNING, 2, time = "6:40"),
            DataItem.Pill(314, "Эрмиталь", "во время еды", Period.EVENING, 4),
            DataItem.Pill(114, "Мебиверин", "за 20 мин. до еды", Period.EVENING, 1),
            DataItem.Pill(21, "Мебиверин", "за 20 мин. до еды", Period.MORNING, 1, "6:20", true),
            DataItem.Pill(15, "Эрмиталь", "во время еды", Period.MORNING, 4),
            DataItem.Pill(28, "Эрмиталь", "во время еды", Period.NOON, 1),
            DataItem.Pill(613, "Бак-Сет", "во время еды", Period.EVENING, 3),
            DataItem.Pill(17, "Гастростат", "перед едой", Period.NOON, 0),
            DataItem.Pill(110, "Омепразол", "за 30 мин. до еды", Period.EVENING, 0),
            DataItem.Pill(212, "Гастростат", "перед едой", Period.EVENING, 2),
            DataItem.Pill(154, "Урсодезоксихол", "перед сном", Period.EVENING, 5)
        )

        private val DEFAULT_PILLS_LIST = listOf(
            DataItem.Pill(12, "Омепразол", "за 30 мин. до еды", Period.MORNING, 0, "6:10", true),
            DataItem.Pill(21, "Мебиверин", "за 20 мин. до еды", Period.MORNING, 1, "6:20", true),
            DataItem.Pill(32, "Гастростат", "перед едой", Period.MORNING, 2, time = "6:40"),
            DataItem.Pill(14, "Бак-Сет", "во время еды", Period.MORNING, 3),
            DataItem.Pill(15, "Эрмиталь", "во время еды", Period.MORNING, 4),
            DataItem.Pill(17, "Гастростат", "перед едой", Period.NOON, 0),
            DataItem.Pill(28, "Эрмиталь", "во время еды", Period.NOON, 1),
            DataItem.Pill(110, "Омепразол", "за 30 мин. до еды", Period.EVENING, 0),
            DataItem.Pill(114, "Мебиверин", "за 20 мин. до еды", Period.EVENING, 1),
            DataItem.Pill(212, "Гастростат", "перед едой", Period.EVENING, 2),
            DataItem.Pill(613, "Бак-Сет", "во время еды", Period.EVENING, 3),
            DataItem.Pill(314, "Эрмиталь", "во время еды", Period.EVENING, 4),
            DataItem.Pill(154, "Урсодезоксихол", "перед сном", Period.EVENING, 5)
            )

        private val DEFAULT_DATA_ITEMS = listOf(
            DataItem.Caption(0,"Утро", Period.MORNING),
            DataItem.Pill(12, "Омепразол", "за 30 мин. до еды", Period.MORNING, 0, "6:10", true),
            DataItem.Pill(21, "Мебиверин", "за 20 мин. до еды", Period.MORNING, 1, "6:20", true),
            DataItem.Pill(32, "Гастростат", "перед едой", Period.MORNING, 2, time = "6:40"),
            DataItem.Pill(14, "Бак-Сет", "во время еды", Period.MORNING, 3),
            DataItem.Pill(15, "Эрмиталь", "во время еды", Period.MORNING, 4),

            DataItem.Caption(1,"Обед", Period.NOON),
            DataItem.Pill(17, "Гастростат", "перед едой", Period.NOON, 0),
            DataItem.Pill(28, "Эрмиталь", "во время еды", Period.NOON, 1),

            DataItem.Caption(2,"Вечер", Period.EVENING),
            DataItem.Pill(110, "Омепразол", "за 30 мин. до еды", Period.EVENING, 0),
            DataItem.Pill(114, "Мебиверин", "за 20 мин. до еды", Period.EVENING, 1),
            DataItem.Pill(212, "Гастростат", "перед едой", Period.EVENING, 2),
            DataItem.Pill(613, "Бак-Сет", "во время еды", Period.EVENING, 3),
            DataItem.Pill(314, "Эрмиталь", "во время еды", Period.EVENING, 4),
            DataItem.Pill(154, "Урсодезоксихол", "перед сном", Period.EVENING, 5)
        )

//        private val DEFAULT_DATA_ITEMS = listOf(
//            DataItem.Caption(0, "Утро", Period.MORNING),
//            DataItem.Pill(1, "Омепразол", "за 30 мин. до еды", Period.MORNING, 0, "6:10", true),
//            DataItem.Pill(2, "Мебиверин", "за 20 мин. до еды", Period.MORNING, 1, "6:20", true),
//            DataItem.Pill(3, "Гастростат", "перед едой", Period.MORNING, 2, time = "6:40"),
//            DataItem.Pill(4, "Бак-Сет", "во время еды", Period.MORNING, 3),
//            DataItem.Pill(5, "Эрмиталь", "во время еды", Period.MORNING, 4),
//
//            DataItem.Caption(6, "Обед", Period.NOON),
//            DataItem.Pill(7, "Гастростат", "перед едой", Period.NOON, 0),
//            DataItem.Pill(8, "Эрмиталь", "во время еды", Period.NOON, 1),
//
//            DataItem.Caption(9, "Вечер", Period.EVENING),
//            DataItem.Pill(10, "Омепразол", "за 30 мин. до еды", Period.EVENING, 0),
//            DataItem.Pill(11, "Мебиверин", "за 20 мин. до еды", Period.EVENING, 1),
//            DataItem.Pill(12, "Гастростат", "перед едой", Period.EVENING, 2),
//            DataItem.Pill(13, "Бак-Сет", "во время еды", Period.EVENING, 3),
//            DataItem.Pill(14, "Эрмиталь", "во время еды", Period.EVENING, 4),
//            DataItem.Pill(15, "Урсодезоксихол", "перед сном", Period.EVENING, 5)
//
//        )
    }
}