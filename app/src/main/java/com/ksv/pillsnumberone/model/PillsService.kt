package com.ksv.pillsnumberone.model

import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.data.RepositoryNew
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PillsService(
    private val pillsDao: PillsDao,
    repository: RepositoryNew
) {
//    private val pillsDB = pillsDao.getAll()
    private val pillsDB = repository.pillDB

    val pillsList = pillsDB.map { listPillsDB ->
        listPillsDB.map { it.toPill() }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )


    fun add(addedPill: DataItem.Pill) {
        val pillWithRightPosition = setLastPosition(addedPill)
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.insert(pillWithRightPosition.toPillDB())
        }
    }

    fun addPills(pills: List<DataItem.Pill>) {
        val withLastPositions = pills.map { setLastPosition(it) }
        val listForDB = withLastPositions.map { it.toPillDB() }
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.insertPills(listForDB)
        }
    }

    fun remove(removedPill: DataItem.Pill) {
        shiftPositions(removedPill.period, removedPill.position)
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.delete(removedPill.toPillDB())
        }
    }

    fun moveUpPillID(id: Long) {
        val movedPill = pillsList.value.firstOrNull { it.id == id }
        movedPill?.let { mvdPll ->
            val previousPill = findPreviousPill(mvdPll)
            previousPill?.let { prvPll ->
                swapPills(mvdPll, prvPll)
            }
        }
    }

    fun moveDownPillID(id: Long) {
        val movedPill = pillsList.value.firstOrNull { it.id == id }
        movedPill?.let { mvdPll ->
            val nextPill = findNextPill(mvdPll)
            nextPill?.let { nxtPll ->
                swapPills(mvdPll, nxtPll)
            }
        }
    }

    fun switchFinishedState(pill: DataItem.Pill) {
        val updatedPill = pill.copy(finished = !pill.finished)
        updatePill(updatedPill)
    }

    fun setTimeFor(id: Long, time: String?) {
        val pill = pillsList.value.firstOrNull { it.id == id }
        pill?.let {
            val newPill = it.copy(time = time)
            updatePill(newPill)
        }
    }

    fun modifyPill(pill: DataItem.Pill) {
        updatePill(pill)
    }

    fun resetPills() {
        val pillsToUpdate = mutableListOf<DataItem.Pill>()
        pillsList.value.onEach { item ->
            if (item.finished || item.time != null) {
                val resetPill = item.copy(finished = false, time = null)
                pillsToUpdate.add(resetPill)
            }
        }
        updatePills(pillsToUpdate)
    }

    private fun updatePill(pill: DataItem.Pill) {
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.update(pill.toPillDB())
        }
    }

    private fun updatePills(pill1: DataItem.Pill, pill2: DataItem.Pill) {
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.updateBoth(pill1.toPillDB(), pill2.toPillDB())
        }
    }

    private fun updatePills(pills: List<DataItem.Pill>) {
        val pillsToDB = pills.map { it.toPillDB() }
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.updateList(pillsToDB)
        }
    }

    private fun setLastPosition(pill: DataItem.Pill): DataItem.Pill {
        val pillsByPeriod = pillsList.value
            .filter { it.period == pill.period }
        val lastPosition = pillsByPeriod.lastIndex + 1
        return pill.copy(position = lastPosition)
    }

    private fun findPreviousPill(movedPill: DataItem.Pill): DataItem.Pill? {
        val prevPos = movedPill.position - 1
        if (prevPos >= 0) {
            return pillsList.value.firstOrNull { it.period == movedPill.period && it.position == prevPos }
        }
        return null
    }

    private fun findNextPill(movedPill: DataItem.Pill): DataItem.Pill? {
        val nextPos = movedPill.position + 1
        return pillsList.value.firstOrNull { it.period == movedPill.period && it.position == nextPos }
    }

    private fun swapPills(pill1: DataItem.Pill, pill2: DataItem.Pill){
        val updatedPill1 = pill1.copy(position = pill2.position)
        val updatedPill2 = pill2.copy(position = pill1.position)
        updatePills(updatedPill1, updatedPill2)
    }

    private fun shiftPositions(period: Period, startIndex: Int) {
        val rebasedList = mutableListOf<DataItem.Pill>()
        pillsList.value.forEach {
            if (it.period == period && it.position > startIndex) {
                val newPill = it.copy(position = it.position - 1)
                rebasedList.add(newPill)
            }
        }
        updatePills(rebasedList)
    }
}