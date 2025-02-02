package com.ksv.pillsnumberone.ui.home.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.model.PillsService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(private val pillService: PillsService) : ViewModel() {
    private val pillsFromDB: StateFlow<List<DataItem.Pill>> = pillService.pills

    private val _dataItems = MutableStateFlow<List<DataItem>>(listOf())
    val dataItems = _dataItems.asStateFlow()

    private val _state = MutableStateFlow<HomeState>(HomeState.Normal)
    val state = _state.asStateFlow()

    init {
        pillsFromDB.onEach {
            _dataItems.value = createDataItemsList(it)
            if (it.isEmpty()) _state.value = HomeState.Empty
            else if (_state.value == HomeState.Empty) _state.value = HomeState.Normal
        }.launchIn(viewModelScope)
    }

    fun moveUp(movedItem: DataItem) {
        if (movedItem is DataItem.Pill)
            pillService.moveUpPill(movedItem)
    }

    fun moveDown(movedItem: DataItem) {
        if (movedItem is DataItem.Pill)
            pillService.moveDownPill(movedItem)
    }

    fun removeItem(removedItem: DataItem) {
        _state.value = HomeState.Normal
        if (removedItem is DataItem.Pill)
            pillService.remove(removedItem)
    }

    fun itemClick(item: DataItem) {
        if (item is DataItem.Pill) {
            when (_state.value) {
                // if now Normal state - change field "finished" on item
                is HomeState.Normal -> pillService.switchFinishedState(item)
                // if some item is Select
                is HomeState.SelectItem -> {
                    val id = (_state.value as HomeState.SelectItem).id
                    if (id == item.id) // click on selected item
                        _state.value = HomeState.ModifyItem(id) // set Modified state
                    else // click on other item
                        _state.value = HomeState.Normal // unSelect item
                }

                else -> {
                    throw IllegalArgumentException("Click on Unable State")
                }
            }
        }
    }

    fun itemLongClick(item: DataItem): Boolean {
        val id = (item as DataItem.Pill).id
        _state.value = HomeState.SelectItem(id)

        return true
    }

    fun onTimeClick(item: DataItem) {
        if (item is DataItem.Pill) {
            if (!item.finished && _state.value !is HomeState.SelectItem) {
                _state.value = HomeState.SetTime(item)
            }
        }
    }

    fun onAddClick() {
        _state.value = HomeState.AddPills
        viewModelScope.launch {
            delay(1000)
            _state.value = HomeState.Normal
        }
    }

    fun editDialogDismiss(id: Long) {
        _state.value = HomeState.SelectItem(id)
    }

    fun setTimeDialogDismiss() {
        _state.value = HomeState.Normal
    }

    fun setTimeFor(itemId: Long, time: String?) {
        pillService.setTimeFor(itemId, time)
    }

    fun onApplyClick() {
        _state.value = HomeState.Normal
    }

    fun modifyPill(pill: DataItem.Pill) {
        pillService.modifyPill(pill)
    }

    fun getPillByID(id: Long): DataItem.Pill? {
        val datItem = _dataItems.value.firstOrNull { it is DataItem.Pill && it.id == id }
        datItem?.let {
            return it as DataItem.Pill
        }
        return null
    }

    fun resetPills() {
        pillService.resetPillsToDefaultState()
        //_state.value = HomeState.Normal
    }

    fun onRefreshButtonClick() {
        if (_state.value is HomeState.Normal) {
            // неплохо бы проверить, а есть ли что рефрешить
            _state.value = HomeState.Refresh
        }
    }

    fun onRefreshDialogDismiss() {
        _state.value = HomeState.Normal
    }

    private fun createDataItemsList(pills: List<DataItem.Pill>): List<DataItem> {
        val dataItemsList = mutableListOf<DataItem>()
        Period.entries.forEach { period ->
            val listByPeriod: MutableList<DataItem> =
                pills.filter { pill -> pill.period == period }
                    .sortedBy { it.position }
                    .toMutableList()

            if (listByPeriod.isNotEmpty()) {
                when (period) {
                    Period.MORNING -> listByPeriod.add(0, MORNING_CAPTION)
                    Period.NOON -> listByPeriod.add(0, NOON_CAPTION)
                    Period.EVENING -> listByPeriod.add(0, EVENING_CAPTION)
                }
                dataItemsList.addAll(listByPeriod)
            }
        }
        return dataItemsList
    }

    companion object {
        private val MORNING_CAPTION = DataItem.PeriodCaption(
            0,
            MyApp.applicationContext.getString(R.string.morning_title),
            Period.MORNING
        )
        private val NOON_CAPTION = DataItem.PeriodCaption(
            1,
            MyApp.applicationContext.getString(R.string.noon_title),
            Period.NOON
        )
        private val EVENING_CAPTION = DataItem.PeriodCaption(
            2,
            MyApp.applicationContext.getString(R.string.evening_title),
            Period.EVENING
        )
    }

}