package com.ksv.pillsnumberone.data

import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.MedicineItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Collections

class Repository {
    private val _actualData = MutableStateFlow<List<DataItem>>(emptyList())
    val actualData = _actualData.asStateFlow()

    init {
        _actualData.value = DEFAULT_DATA_ITEMS
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

    fun moveUpItem(movedItem: DataItem) {
        val indexOfMoved = _actualData.value.indexOf(movedItem)
        if (indexOfMoved > 0) { // исключаем 0 потому, что 0 должен быть у Caption'a
            if (canNotBeMoveUp(indexOfMoved)) return
            val newList = _actualData.value.toMutableList()
            Collections.swap(newList, indexOfMoved, indexOfMoved - 1)
//            newList.forEachIndexed { ind, item ->
//                item.index = ind
//            }
            _actualData.value = newList.toList()
        }
    }

    fun moveDownItem(movedItem: DataItem) {
        val indexOfMoved = _actualData.value.indexOf(movedItem)
        if (indexOfMoved > 0) {
            if (canNotBeMoveDown(indexOfMoved)) return
            val newList = _actualData.value.toMutableList()
            Collections.swap(newList, indexOfMoved, indexOfMoved + 1)
//            newList.forEachIndexed { ind, item ->
//                item.index = ind
//            }
            _actualData.value = newList.toList()
        }
    }

    private fun canNotBeMoveUp(indexOfMoved: Int): Boolean{
        val previousItem = _actualData.value[indexOfMoved - 1]
        return previousItem is DataItem.Caption
    }

    private fun canNotBeMoveDown(indexOfMoved: Int): Boolean{
        if(indexOfMoved == _actualData.value.lastIndex) return true
        val nextItem = _actualData.value[indexOfMoved + 1]
        return nextItem is DataItem.Caption
    }

    fun remove(dataItem: DataItem) {
        val newList = _actualData.value.filter { it != dataItem }
//        newList.forEachIndexed { ind, item ->
//            item.index = ind
//        }
        _actualData.value = newList
    }

    companion object {
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