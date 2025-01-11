package com.ksv.pillsnumberone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TestViewModel(private val dataItemService: DataItemService): ViewModel() {

    private val dataFromDB = dataItemService.dataItemList
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

    init {
        dataFromDB.onEach {
            _actualData.value = includeEditableItem(it)
        }.launchIn(viewModelScope)
    }


    fun addItem(pill: DataItem.Pill){
        dataItemService.add(pill)
    }

    fun moveUp(movedItem: DataItem) {
        if(movedItem is DataItem.Pill)
            dataItemService.moveUpItemID(movedItem.id)
    }
    fun moveDown(movedItem: DataItem){
        if(movedItem is DataItem.Pill)
            dataItemService.moveDownItemID(movedItem.id)
    }
    fun removeItem(removedItem: DataItem){
        finishEditMode()
        dataItemService.remove(removedItem)
    }
    fun itemClick(item: DataItem){
        if(editableItemId == null) {
            dataItemService.switchFinished(item)
        }else if(item is DataItem.Pill && item.id == editableItemId){
            _modifiedItem.value = item
        } else {
            finishEditMode()
        }
    }
    fun itemLongClick(item: DataItem){
        if(editableItemId == null){
            editableItemId = (item as DataItem.Pill).id
            _actualData.value = includeEditableItem(_actualData.value)
        }
    }
    fun setTimeClick(item: DataItem){
        if(editableItemId == null && item is DataItem.Pill) {
            _setTimeFor.value = item
        }
    }
    fun setTimeFinished(){
        _setTimeFor.value = null
    }
    fun setTimeFor(itemId: Long, time: String){
        dataItemService.setTimeFor(itemId, time)
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
        dataItemService.modifyPill(pill)
    }
    fun getPillByID(id: Long): DataItem.Pill?{
        val datItem = _actualData.value.firstOrNull { it is DataItem.Pill && it.id == id }
        datItem?.let {
            return it as DataItem.Pill
        }
        return null
    }
    fun resetPills(){
        dataItemService.resetPills()
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