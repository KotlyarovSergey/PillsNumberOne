package com.ksv.pillsnumberone.model

import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataItemService2(private val pillsDao: PillsDao) {
    private val pillsDB = pillsDao.getAll()

    val dataItemList = pillsDB.map { listPillsDB ->
        makeDataList(listPillsDB.map { it.toPill() })
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _isEditMode = MutableStateFlow<Boolean>(false)
    val isEditMode = _isEditMode.asStateFlow()

    init {
        dataItemList.onEach {
            _isEditMode.value = checkEdit()
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    fun add(addedPill: DataItem.Pill) {
        val pillWithRightPosition = setPosition(addedPill)
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.insert(pillWithRightPosition.toPillDB())
        }
    }

    fun remove(deletedItem: DataItem) {
        if (deletedItem is DataItem.Pill) {
            CoroutineScope(Dispatchers.Default).launch {
                pillsDao.delete(deletedItem.toPillDB())
            }
        }
    }

    fun moveUpItem(movedItem: DataItem) {
        val indexOfMoved = dataItemList.value.indexOf(movedItem)
        if (indexOfMoved > 0) { // исключаем 0, потому что 0 должен быть у Caption'a
            if (movedItem is DataItem.Pill) {
                if (canBeMoveUp(indexOfMoved)) {
                    val previousItem = dataItemList.value[indexOfMoved - 1] as DataItem.Pill
                    val updatedMovedItem = movedItem.copy(position = previousItem.position)
                    val updatedPreviousItem = previousItem.copy(position = movedItem.position)
                    updatePill(updatedMovedItem)
                    updatePill(updatedPreviousItem)
                }
            }
        }
    }

    fun moveDownItem(movedItem: DataItem) {
        val indexOfMoved = dataItemList.value.indexOf(movedItem)
        if (indexOfMoved > 0) {
            if (movedItem is DataItem.Pill) {
                if (canBeMoveDown(indexOfMoved)) {
                    val nextItem = dataItemList.value[indexOfMoved + 1] as DataItem.Pill
                    val updatedNextItem = nextItem.copy(position = movedItem.position)
                    val updatedMovedItem = movedItem.copy(position = nextItem.position)
                    updatePill(updatedMovedItem)
                    updatePill(updatedNextItem)
                }
            }
        }
    }

    fun onClick(clickedItem: DataItem) {
        if (clickedItem is DataItem.Pill) {
            val updatedItem = clickedItem.copy(finished = !clickedItem.finished)
            updatePill(updatedItem)
        }
    }

    fun longClick(clickedItem: DataItem) {
        val indexOfClicked = dataItemList.value.indexOf(clickedItem)
        if (indexOfClicked > 0) {
            if (clickedItem is DataItem.Pill) {
                if (itCanBeEdit(indexOfClicked)) {
                    val newItem = clickedItem.copy(editable = true)
                    updatePill(newItem)
                }
            }
        }
    }

    fun finishEditionForAll() {
        dataItemList.value.forEach {
        if (it is DataItem.Pill && it.editable) {
            val newItem = it.copy(editable = false)
            updatePill(newItem)
        }
        }
    }

    fun setTimeFor(item: DataItem, time: String) {
        val indexOfItem = dataItemList.value.indexOf(item)
        if (indexOfItem > 0) {
            if (item is DataItem.Pill) {
                val itemWithTime = item.copy(time = time)
                updatePill(itemWithTime)
            }
        }
    }




    private fun makeDataList(pills: List<DataItem.Pill>): List<DataItem> {
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
            toList()
        }
    }

    private fun updatePill(pill: DataItem.Pill) {
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.update(pill.toPillDB())
        }
    }

    private fun checkEdit(): Boolean {
        dataItemList.value.forEach {
            if (it is DataItem.Pill) {
                if (it.editable)
                    return true
            }
        }
        return false
    }

    private fun setPosition(pill: DataItem.Pill): DataItem.Pill {
        val pillsByPeriod = dataItemList.value
            .filter { it is DataItem.Pill && it.period == pill.period }
        val lastPosition = pillsByPeriod.lastIndex + 1
        return pill.copy(position = lastPosition)
    }

    private fun itCanBeEdit(checkedIndex: Int): Boolean {
        dataItemList.value.forEachIndexed { index, item ->
            if (item is DataItem.Pill) {
                if (item.editable && index != checkedIndex)
                    return false
            }
        }
        return true
    }

    private fun canNotBeMoveUp(indexOfMoved: Int): Boolean {
        val previousItem = dataItemList.value[indexOfMoved - 1]
        return previousItem is DataItem.Caption
    }

    private fun canBeMoveUp(indexOfMoved: Int): Boolean {
        val previousItem = dataItemList.value[indexOfMoved - 1]
        return previousItem !is DataItem.Caption
    }

    private fun canNotBeMoveDown(indexOfMoved: Int): Boolean {
        if (indexOfMoved == dataItemList.value.lastIndex) return true
        val nextItem = dataItemList.value[indexOfMoved + 1]
        return nextItem is DataItem.Caption
    }

    private fun canBeMoveDown(indexOfMoved: Int): Boolean {
        if (indexOfMoved == dataItemList.value.lastIndex) return false
        val nextItem = dataItemList.value[indexOfMoved + 1]
        return nextItem !is DataItem.Caption
    }


    companion object {
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