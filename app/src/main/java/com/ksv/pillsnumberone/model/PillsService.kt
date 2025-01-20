package com.ksv.pillsnumberone.model

import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class PillsService(private val repository: Repository) {
    private val pillsDB = repository.pillDB

    val pillsList = pillsDB.map { listPillsDB ->
        listPillsDB.map { it.toPill() }
    }.onEach {
        repairPositions(it)
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun add(addedPill: DataItem.Pill) {
        val pillWithRightPosition = setLastPosition(addedPill)
        repository.insert(pillWithRightPosition)
    }
    fun add(pills: List<DataItem.Pill>) {
        val withLastPositions = pills.map { setLastPosition(it) }
        repository.insert(withLastPositions)
    }
    fun remove(removedPill: DataItem.Pill) {
        decreasePositionsAfter(removedPill.period, removedPill.position)
        repository.remove(removedPill)
    }
    fun moveUpPill(movedPill: DataItem.Pill) {
        previousPillOrNull(movedPill)?.let { previousPill ->
            swapPills(movedPill, previousPill)
        }
    }
    fun moveDownPill(movedPill: DataItem.Pill) {
        nextPillOrNull(movedPill)?.let { nextPill ->
            swapPills(movedPill, nextPill)
        }
    }
    fun switchFinishedState(pill: DataItem.Pill) {
        val modifiedPill = pill.copy(finished = !pill.finished)
        repository.update(modifiedPill)
    }
    fun setTimeFor(id: Long, time: String?) {
        val pill = pillsList.value.firstOrNull { it.id == id }
        pill?.let {
            val modifiedPill = it.copy(time = time)
            repository.update(modifiedPill)
        }
    }
    fun modifyPill(pill: DataItem.Pill) {
        repository.update(pill)
    }
    fun resetPillsToDefaultState() {
        val pillsToUpdate = mutableListOf<DataItem.Pill>()
        pillsList.value.onEach { pill ->
            if (pill.finished || pill.time != null) {
                val defaultsPill = pill.copy(finished = false, time = null)
                pillsToUpdate.add(defaultsPill)
            }
        }
        repository.update(pillsToUpdate)
    }


    private fun repairPositions(pills: List<DataItem.Pill>) {
        Period.entries.forEach { period ->
            val listByPeriod = pills.filter { it.period == period }
            if (listByPeriod.isNotEmpty()) {
                val doublePositionContains =
                    listByPeriod.map { it.position }.toSet().size != listByPeriod.size
                val skippedPositionContains =
                    listByPeriod.maxByOrNull { it.position }!!.position != listByPeriod.size - 1
                if (doublePositionContains || skippedPositionContains) {
                    var ind = 0
                    val rebased = listByPeriod
                        .sortedBy { it.position }
                        .map { it.copy(position = ind++) }
                    repository.update(rebased)
                }
            }
        }
    }
    private fun setLastPosition(pill: DataItem.Pill): DataItem.Pill {
        val pillsByPeriod = pillsList.value
            .filter { it.period == pill.period }
        val lastPosition = pillsByPeriod.lastIndex + 1
        return pill.copy(position = lastPosition)
    }
    private fun previousPillOrNull(movedPill: DataItem.Pill): DataItem.Pill? {
        val prevPos = movedPill.position - 1
        if (prevPos >= 0) {
            return pillsList.value.firstOrNull { it.period == movedPill.period && it.position == prevPos }
        }
        return null
    }
    private fun nextPillOrNull(movedPill: DataItem.Pill): DataItem.Pill? {
        val nextPos = movedPill.position + 1
        return pillsList.value.firstOrNull { it.period == movedPill.period && it.position == nextPos }
    }
    private fun swapPills(pill1: DataItem.Pill, pill2: DataItem.Pill) {
        val updatedPill1 = pill1.copy(position = pill2.position)
        val updatedPill2 = pill2.copy(position = pill1.position)
        repository.update(listOf(updatedPill1, updatedPill2))
    }
    private fun decreasePositionsAfter(period: Period, startIndex: Int) {
        val modifiedList = mutableListOf<DataItem.Pill>()
        pillsList.value.forEach { pill ->
            if (pill.period == period && pill.position > startIndex) {
                val modifiedPill = pill.copy(position = pill.position - 1)
                modifiedList.add(modifiedPill)
            }
        }
        repository.update(modifiedList)
    }
}