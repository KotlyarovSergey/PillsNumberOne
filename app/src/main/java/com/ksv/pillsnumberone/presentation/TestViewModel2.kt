package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class TestViewModel2(private val pillsDao: PillsDao): ViewModel() {
    private val dataFromDB = pillsDao.getAll().map { listPillsDB ->
        DataItemService2.makeDataList(listPillsDB.map { it.toPill() })
    }

    private val _actualData = MutableStateFlow<List<DataItem>>(listOf())
    val actualData = _actualData.asStateFlow()

    private var editableItemId: Long? = null
        set(value) {
            field = value
            _isEditMode.value = field != null
        }

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private val _setTimeFor = MutableStateFlow<DataItem?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()


    init {
        dataFromDB.onEach {
            _actualData.value = includeEditableItem(it)
        }.launchIn(viewModelScope)
    }



    fun addItem(pill: DataItem.Pill){
        DataItemService2(actualData.value, pillsDao).add(pill)
    }
    fun moveUp(movedItem: DataItem) {
        DataItemService2(actualData.value, pillsDao).moveUpItem(movedItem)
    }
    fun moveDown(movedItem: DataItem){
        DataItemService2(actualData.value, pillsDao).moveDownItem(movedItem)
    }
    fun removeItem(removedItem: DataItem){
        finishEditMode()
        DataItemService2(actualData.value, pillsDao).remove(removedItem)
    }
    fun itemClick(item: DataItem){
        if(editableItemId == null) {
            DataItemService2(actualData.value, pillsDao).switchFinished(item)
        }
    }
    fun itemLongClick(item: DataItem){
        if(editableItemId == null){
            editableItemId = (item as DataItem.Pill).id
            _actualData.value = includeEditableItem(_actualData.value)
        }
    }
    fun setTimeClick(item: DataItem){
        if(editableItemId == null) {
            _setTimeFor.value = item
        }
    }
    fun setTimeFinished(){
        _setTimeFor.value = null
    }
    fun setTime(time: String){
        _setTimeFor.value?.let { item ->
            DataItemService2(actualData.value, pillsDao).setTimeFor(item, time)
        }
    }
    fun finishEditMode(){
        editableItemId?.let {
            resetEditionForItem(editableItemId!!)
            editableItemId = null
        }
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
}