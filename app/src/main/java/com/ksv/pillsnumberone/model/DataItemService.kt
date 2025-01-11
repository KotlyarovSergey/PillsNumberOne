package com.ksv.pillsnumberone.model

import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataItemService(private val pillsDao: PillsDao) {
    private val pillsDB = pillsDao.getAll()

    val dataItemList = pillsDB.map { listPillsDB ->
        makeDataList(listPillsDB.map { it.toPill() })
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )



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
            reBasePositionsInPeriod(deletedItem)
        }
    }

    fun moveUpItemID(id: Long) {
        val indexOfMoved = dataItemList.value.indexOfFirst { it is DataItem.Pill && it.id == id }
        if (indexOfMoved > 0) { // исключаем 0, потому что 0 должен быть у Caption'a
            if (canBeMoveUp(indexOfMoved)) {
                val movedItem = dataItemList.value[indexOfMoved] as DataItem.Pill
                val previousItem = dataItemList.value[indexOfMoved - 1] as DataItem.Pill
                val updatedMovedItem = movedItem.copy(position = previousItem.position)
                val updatedPreviousItem = previousItem.copy(position = movedItem.position)
                updatePill(updatedMovedItem)
                updatePill(updatedPreviousItem)
            }
        }
    }

    fun moveDownItemID(id: Long) {
        val indexOfMoved = dataItemList.value.indexOfFirst { it is DataItem.Pill && it.id == id }
        if (indexOfMoved > 0) {
            if (canBeMoveDown(indexOfMoved)) {
                val movedItem = dataItemList.value[indexOfMoved] as DataItem.Pill
                val nextItem = dataItemList.value[indexOfMoved + 1] as DataItem.Pill
                val updatedNextItem = nextItem.copy(position = movedItem.position)
                val updatedMovedItem = movedItem.copy(position = nextItem.position)
                updatePill(updatedMovedItem)
                updatePill(updatedNextItem)
            }

        }
    }

    fun switchFinished(finishedItem: DataItem) {
        if (finishedItem is DataItem.Pill) {
            val updatedItem = finishedItem.copy(finished = !finishedItem.finished)
            updatePill(updatedItem)
        }
    }

    fun setTimeFor(itemId: Long, time: String?) {
        val item = dataItemList.value.firstOrNull{ it is DataItem.Pill && it.id == itemId}
        item?.let {
            val oldPill = it as DataItem.Pill
            val newPill = oldPill.copy(time = time)
            updatePill(newPill)
        }
    }

    fun modifyPill(pill: DataItem.Pill){
        updatePill(pill)
    }

    fun resetPills(){
        dataItemList.value.onEach { item ->
            if(item is DataItem.Pill){
                if(item.finished || item.time != null) {
                    val resetPill = item.copy(finished = false, time = null)
                    updatePill(resetPill)
                }
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

    private fun setPosition(pill: DataItem.Pill): DataItem.Pill {
        val pillsByPeriod = dataItemList.value
            .filter { it is DataItem.Pill && it.period == pill.period }
        val lastPosition = pillsByPeriod.lastIndex + 1
        return pill.copy(position = lastPosition)
    }

    private fun canBeMoveUp(indexOfMoved: Int): Boolean {
        val previousItem = dataItemList.value[indexOfMoved - 1]
        return previousItem !is DataItem.Caption
    }

    private fun canBeMoveDown(indexOfMoved: Int): Boolean {
        if (indexOfMoved == dataItemList.value.lastIndex) return false
        val nextItem = dataItemList.value[indexOfMoved + 1]
        return nextItem !is DataItem.Caption
    }

    private fun reBasePositionsInPeriod(deletedItem: DataItem.Pill){
        val period = deletedItem.period
        val deletedPosition = deletedItem.position
        dataItemList.value.forEach {
            if (it is DataItem.Pill && it.period == period && it.position > deletedPosition){
                val newPill = it.copy(position = it.position - 1)
                updatePill(newPill)
            }
        }
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