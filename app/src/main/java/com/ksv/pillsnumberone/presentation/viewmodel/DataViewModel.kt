package com.ksv.pillsnumberone.presentation.viewmodel

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

//class DataViewModel(private val dataItemService: DataItemService): ViewModel() {
class DataViewModel(private val pillService: PillsService): ViewModel() {

//    private val dataFromDB = pillService.dataItemList
    private val pillsFromDB = pillService.pillsList
    private val _actualData = MutableStateFlow<List<DataItem>>(listOf())
    val actualData = _actualData.asStateFlow()

    private var editableItemId: Long? = null
        set(value) {
            field = value
            _isEditMode.value = field != null
        }

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private val _modifiedItem = MutableStateFlow<DataItem?>(null)
    val modifiedItem = _modifiedItem.asStateFlow()

    private val _setTimeFor = MutableStateFlow<DataItem.Pill?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()

    private val _emptyDataHint = MutableStateFlow(false)
    val emptyDataHint = _emptyDataHint.asStateFlow()

    init {
        pillsFromDB.onEach {
            val dataItemList = makeDataList(it)
            _actualData.value = includeEditableItem(dataItemList)
            _emptyDataHint.value = _actualData.value.isEmpty()
        }.launchIn(viewModelScope)
    }


    fun addItem(pill: DataItem.Pill){
        pillService.add(pill)
    }

    fun moveUp(movedItem: DataItem) {
        if(movedItem is DataItem.Pill)
            pillService.moveUpPillID(movedItem.id)
    }
    fun moveDown(movedItem: DataItem){
        if(movedItem is DataItem.Pill)
            pillService.moveDownPillID(movedItem.id)
    }
    fun removeItem(removedItem: DataItem){
        finishEditMode()
        if(removedItem is DataItem.Pill)
            pillService.remove(removedItem)
    }
    fun itemClick(item: DataItem){
        if(item is DataItem.Pill) {
            if (editableItemId == null) {
                pillService.switchFinishedState(item)
            } else if (item.id == editableItemId) {
                _modifiedItem.value = item
            } else {
                finishEditMode()
            }
        }
    }
    fun itemLongClick(item: DataItem): Boolean{
        if(editableItemId == null){
            editableItemId = (item as DataItem.Pill).id
            _actualData.value = includeEditableItem(_actualData.value)
        }
        return true
    }
    fun onTimeClick(item: DataItem){
        if(item is DataItem.Pill){
            if (!item.finished && !_isEditMode.value){
                _setTimeFor.value = item
            }
        }
    }
    fun setTimeFinished(){
        _setTimeFor.value = null
    }
    fun setTimeFor(itemId: Long, time: String?){
        pillService.setTimeFor(itemId, time)
    }
    fun finishEditMode(){
        editableItemId?.let {
            resetEditionForItem(editableItemId!!)
            editableItemId = null
        }
    }
    fun resetModifiedItem(){
        _modifiedItem.value = null
    }
    fun modifyPill(pill: DataItem.Pill){
        pillService.modifyPill(pill)
    }
    fun getPillByID(id: Long): DataItem.Pill?{
        val datItem = _actualData.value.firstOrNull { it is DataItem.Pill && it.id == id }
        datItem?.let {
            return it as DataItem.Pill
        }
        return null
    }
    fun resetPills(){
        pillService.resetPills()
    }



    private fun includeEditableItem(data: List<DataItem>): List<DataItem>{
        if(editableItemId != null) {
            val index = data.indexOfFirst { it is DataItem.Pill && it.id == editableItemId }
            if(index != -1 ){
                val editablePill = (data[index] as DataItem.Pill).copy(editable = true)
                return data.toMutableList().apply {
                    this[index] = editablePill
                }
            }
        }
        return data
    }

    private fun resetEditionForItem(itemId: Long){
        val index = _actualData.value.indexOfFirst { it is DataItem.Pill && it.id == itemId }
        if(index != -1){
            val unEditablePill = (_actualData.value[index] as DataItem.Pill).copy(editable = false)
            _actualData.value = _actualData.value.toMutableList().apply {
                this[index] = unEditablePill
                this.toList()
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
        }
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