package com.ksv.pillsnumberone.ui.home.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.model.PillsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(private val pillService: PillsService) : ViewModel() {

    private val pillsFromDB = pillService.pillsList

    private val _actualData = MutableStateFlow<List<DataItem>>(listOf())
    val actualData = _actualData.asStateFlow()

    private var selectedItemId: Long? = null
        set(value) {
            field = value
            _isEditMode.value = field != null
        }

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private val _modifiedPill = MutableStateFlow<DataItem.Pill?>(null)
    val modifiedPill = _modifiedPill.asStateFlow()

    private val _setTimeFor = MutableStateFlow<DataItem.Pill?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()

    private val _showEmptyDataHint = MutableStateFlow(false)
    val showEmptyDataHint = _showEmptyDataHint.asStateFlow()


    private val _state = MutableStateFlow<HomeState>(HomeState.Normal)
    val state = _state.asStateFlow()

    init {
        pillsFromDB.onEach {
            _actualData.value = createDataItemsList(it)
            includeSelectedPillIntoActualData()
            _showEmptyDataHint.value = _actualData.value.isEmpty()
            _state.value = if (it.isEmpty()) HomeState.Empty else HomeState.Normal

        }.launchIn(viewModelScope)
    }


    fun addItem(pill: DataItem.Pill) {
        pillService.add(pill)
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
        finishEditMode()
        if (removedItem is DataItem.Pill)
            pillService.remove(removedItem)
    }

    fun itemClick(item: DataItem) {
        if (item is DataItem.Pill) {
            if (isEditMode.value) {
                if (item.id == selectedItemId) {
                    _modifiedPill.value = item
                } else {
                    finishEditMode()
                }
            } else {
                pillService.switchFinishedState(item)
            }
        }
    }

    fun itemLongClick(item: DataItem): Boolean {
        if (item is DataItem.Pill) {
            if (item.id != selectedItemId){
                if (selectedItemId != null) {
                    unselectItem(selectedItemId!!)
                }
                selectedItemId = item.id
                includeSelectedPillIntoActualData()
            }
        }
        return true
    }

    fun onTimeClick(item: DataItem) {
        if (item is DataItem.Pill) {
            if (!item.finished && !_isEditMode.value) {
                _setTimeFor.value = item
            }
        }
    }

    fun setTimeDialogShowed() {
        _setTimeFor.value = null
    }

    fun editDialogShowed() {
        _modifiedPill.value = null
    }

    fun setTimeFor(itemId: Long, time: String?) {
        pillService.setTimeFor(itemId, time)
    }

    fun onApplyClick() {
        finishEditMode()
    }

    fun resetModifiedItem() {
        _modifiedPill.value = null
    }

    fun modifyPill(pill: DataItem.Pill) {
        pillService.modifyPill(pill)
    }

    fun getPillByID(id: Long): DataItem.Pill? {
        val datItem = _actualData.value.firstOrNull { it is DataItem.Pill && it.id == id }
        datItem?.let {
            return it as DataItem.Pill
        }
        return null
    }

    fun resetPills() {
        pillService.resetPillsToDefaultState()
    }


    private fun includeEditableItemToActualData(data: List<DataItem>): List<DataItem> {
        if (selectedItemId != null) {
            val index = data.indexOfFirst { it is DataItem.Pill && it.id == selectedItemId }
            if (index != -1) {
                val editablePill = (data[index] as DataItem.Pill).copy(editable = true)
                return data.toMutableList().apply {
                    this[index] = editablePill
                }
            }
        }
        return data
    }

    private fun includeSelectedPillIntoActualData(pill: DataItem.Pill) {
        val index = _actualData.value.indexOfFirst { it is DataItem.Pill && it.id == pill.id }
        if (index != -1) {
            val selectedPill = (_actualData.value[index] as DataItem.Pill).copy(editable = true)
            _actualData.value = _actualData.value.toMutableList().apply {
                this[index] = selectedPill
            }
        }
    }

    private fun includeSelectedPillIntoActualData() {
        selectedItemId?.let {
            val index =
                _actualData.value.indexOfFirst { it is DataItem.Pill && it.id == selectedItemId }
            if (index != -1) {
                val selectedPill = (_actualData.value[index] as DataItem.Pill).copy(editable = true)
                _actualData.value = _actualData.value.toMutableList().apply {
                    this[index] = selectedPill
                }
            }
        }
    }

    private fun finishEditMode() {
        //_modifiedPill.value = null
        selectedItemId?.let {
            unselectItem(selectedItemId!!)
            selectedItemId = null
        }
    }

    private fun unselectItem(itemId: Long) {
        val index = _actualData.value.indexOfFirst { it is DataItem.Pill && it.id == itemId }
        if (index != -1) {
            val unEditablePill = (_actualData.value[index] as DataItem.Pill).copy(editable = false)
            _actualData.value = _actualData.value.toMutableList().apply {
                this[index] = unEditablePill
                this.toList()
            }
        }
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