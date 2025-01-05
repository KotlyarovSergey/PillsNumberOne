package com.ksv.pillsnumberone.model

import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Collections

class DataItemService(private val items: MutableStateFlow<List<DataItem>>) {
    //class DataItemService(private val items: List<DataItem>) {

    private val _isEditMode = MutableStateFlow<Boolean>(false)
    val isEditMode = _isEditMode.asStateFlow()


    fun add(addedPill: DataItem.Pill) {
        val lastIndex = items.value.indexOfLast {
            it is DataItem.Pill && it.period == addedPill.period
        }
//        if (lastIndex > 0) {
            val newIndex = lastIndex + 1
            items.value = items.value.toMutableList().apply {
                val indexedItem = addedPill.copy(position = newIndex)
                this.add(newIndex, indexedItem)
            }
//        }
        if (captionOfPeriodNotExist(addedPill.period)){
            addCaptionInPeriod(addedPill.period)
        }
    }

    fun makeDataList(pills: List<DataItem.Pill>): List<DataItem> {
        val morningPills: MutableList<DataItem> = pills
            .filter { pill -> pill.period == Period.MORNING }
            .sortedBy { it.position }
            .toMutableList()
        if (morningPills.isNotEmpty()) morningPills.add(0, MORNING_CAPTION)

        val noonPills: MutableList<DataItem> = pills
            .filter { it.period == Period.NOON }
            .sortedBy { it.position }
            .toMutableList()
        if (noonPills.isNotEmpty()) noonPills.add(0, NOON_CAPTION)

        val eveningPills: MutableList<DataItem> = pills
            .filter { it.period == Period.EVENING }
            .sortedBy { it.position }
            .toMutableList()
        if (eveningPills.isNotEmpty()) eveningPills.add(0, EVENING_CAPTION)

        return mutableListOf<DataItem>().apply {
            addAll(morningPills)
            addAll(noonPills)
            addAll(eveningPills)
        }
    }



    fun moveUpItem(movedItem: DataItem) {
        val indexOfMoved = items.value.indexOf(movedItem)
        if (indexOfMoved > 0) { // исключаем 0 потому, что 0 должен быть у Caption'a
            if (canNotBeMoveUp(indexOfMoved)) return
            val newList = items.value.toMutableList()
            Collections.swap(newList, indexOfMoved, indexOfMoved - 1)
            items.value = newList.toList()
            reDefinePositions((movedItem as DataItem.Pill).period)
        }
    }

    fun moveDownItem(movedItem: DataItem) {
        val indexOfMoved = items.value.indexOf(movedItem)
        if (indexOfMoved > 0) {
            if (canNotBeMoveDown(indexOfMoved)) return
            val newList = items.value.toMutableList()
            Collections.swap(newList, indexOfMoved, indexOfMoved + 1)
            items.value = newList.toList()
            reDefinePositions((movedItem as DataItem.Pill).period)
        }
    }

    fun remove(deletedItem: DataItem) {
        val indexOfDeleted = items.value.indexOf(deletedItem)
        if (indexOfDeleted > 0) {
            val newList = items.value.toMutableList().apply {
                removeAt(indexOfDeleted)
            }
            items.value = newList.toList()
//            removeEmptyCaptions()
            removeEmptyCaptionByPeriod((deletedItem as DataItem.Pill).period)
            reDefinePositions((deletedItem as DataItem.Pill).period)
        }
    }

    fun click(clickedItem: DataItem) {
        val indexOfClicked = items.value.indexOf(clickedItem)
        if (indexOfClicked > 0) {
            if (clickedItem is DataItem.Pill) {
                val finished = clickedItem.finished
                items.value = items.value.toMutableList().apply {
                    val newItem = clickedItem.copy(finished = !finished)
                    this[indexOfClicked] = newItem
                }
            }
        }
    }

    fun longClick(clickedItem: DataItem) {
        val indexOfClicked = items.value.indexOf(clickedItem)
        if (indexOfClicked > 0) {
            if (clickedItem is DataItem.Pill) {
                if (canBeEdit(indexOfClicked)) {
                    if (!clickedItem.editable) {
                        items.value = items.value.toMutableList().apply {
                            val newItem = clickedItem.copy(editable = true)
                            this[indexOfClicked] = newItem
                        }
                        _isEditMode.value = true
                    }
                }
            }
        }
    }

    fun finishEditionForAll() {
        items.value = items.value.toMutableList().apply {
            this.forEachIndexed { index, item ->
                if (item is DataItem.Pill) {
                    val newItem = item.copy(editable = false)
                    this[index] = newItem
                }
            }
        }
        _isEditMode.value = false
    }

    fun setTimeFor(item: DataItem, time: String) {
        if (item is DataItem.Pill) {
            val indexOfItem = items.value.indexOf(item)
            if (indexOfItem > 0) {
                items.value = items.value.toMutableList().apply {
                    val new = item.copy(time = time)
                    this[indexOfItem] = new
                }
            }
        }
    }



    private fun canNotBeMoveUp(indexOfMoved: Int): Boolean {
        val previousItem = items.value[indexOfMoved - 1]
        return previousItem is DataItem.Caption
    }

    private fun canNotBeMoveDown(indexOfMoved: Int): Boolean {
        if (indexOfMoved == items.value.lastIndex) return true
        val nextItem = items.value[indexOfMoved + 1]
        return nextItem is DataItem.Caption
    }

    private fun canBeEdit(checkedIndex: Int): Boolean {
        items.value.forEachIndexed { index, item ->
            if (item is DataItem.Pill) {
                if (item.editable && index != checkedIndex)
                    return false
            }
        }
        return true
    }

    private fun reDefinePositions(period: Period) {
        items.value = items.value.toMutableList().apply {
            this.forEachIndexed { index, item ->
                if (item is DataItem.Pill && item.period == period) {
                    val new = item.copy(position = index)
                    this[index] = new
                }
            }
        }
    }

    private fun removeEmptyCaptions() {
        val periods = listOf(Period.MORNING, Period.NOON, Period.EVENING)
        periods.forEach { period ->
            val listByPeriod = items.value.filter { it is DataItem.Pill && it.period == period }
            if (listByPeriod.isEmpty()) {
                val removedCaption =
                    items.value.find { it is DataItem.Caption && it.period == period }
                removedCaption?.let {
                    val newList = items.value.toMutableList().apply {
                        remove(removedCaption)
                    }
                    items.value = newList.toList()
                }
            }
        }
    }

    private fun removeEmptyCaptionByPeriod(period: Period) {
        val listByPeriod = items.value.filter { it is DataItem.Pill && it.period == period }
        if (listByPeriod.isEmpty()) {
            val removedCaption = items.value.find { it is DataItem.Caption && it.period == period }
            removedCaption?.let {
                val newList = items.value.toMutableList().apply {
                    remove(removedCaption)
                }
                items.value = newList.toList()
            }
        }
    }

    private fun captionOfPeriodNotExist(period: Period): Boolean {
        items.value.forEach {
            if (it is DataItem.Caption && it.period == period)
                return false
        }
        return true
    }

    private fun addCaptionInPeriod(period: Period) {
        val firstInPeriod = items.value.firstOrNull { it is DataItem.Pill && it.period == period }
        firstInPeriod?.let { first ->
            val index = items.value.indexOf(first)
            val caption = when (period) {
                Period.MORNING -> MORNING_CAPTION
                Period.NOON -> NOON_CAPTION
                Period.EVENING -> EVENING_CAPTION
            }
            items.value = items.value.toMutableList().apply {
                add(index, caption)
            }
        }
    }



    companion object {
       // private val MORNING_CAPTION = DataItem.Caption(0, "Утро", Period.MORNING)
       // private val NOON_CAPTION = DataItem.Caption(1,"Обед", Period.NOON)
       // private val EVENING_CAPTION = DataItem.Caption(2, "Вечер", Period.EVENING)
        private val MORNING_CAPTION = DataItem.Caption(
            0,
            MyApp.applicationContext.getString(R.string.morning_title),
            Period.MORNING
        )
        private val NOON_CAPTION = DataItem.Caption(
            1,
            MyApp.applicationContext.getString(R.string.noon_title),
            Period.NOON
        )
        private val EVENING_CAPTION = DataItem.Caption(
            2,
            MyApp.applicationContext.getString(R.string.evening_title),
            Period.EVENING
        )
    }

}
