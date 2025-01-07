package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TestViewModel(private val dataItemService2: DataItemService2): ViewModel() {

    val actualData = dataItemService2.dataItemList
    val isEditMode = dataItemService2.isEditMode

    private val _setTimeFor = MutableStateFlow<DataItem?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()

    private val _editableItem = MutableStateFlow<DataItem?>(null)
    val editableItem = _editableItem.asStateFlow()


    fun addItem(pill: DataItem.Pill){
        dataItemService2.add(pill)
    }

    fun moveUp(movedItem: DataItem) {
        dataItemService2.moveUpItem(movedItem)
    }
    fun moveDown(movedItem: DataItem){
        dataItemService2.moveDownItem(movedItem)
    }
    fun removeItem(removedItem: DataItem){
        dataItemService2.remove(removedItem)
        finishEditMode()
    }
    fun itemClick(item: DataItem){
        dataItemService2.switchFinished(item)
    }
    fun itemLongClick(item: DataItem){

        dataItemService2.setEditable(item)
    }
    fun setTimeClick(item: DataItem){
        _setTimeFor.value = item
    }
    fun setTimeFinished(){
        _setTimeFor.value = null
    }
    fun setTime(time: String){
        _setTimeFor.value?.let { item ->
            dataItemService2.setTimeFor(item, time)
        }
    }
    fun finishEditMode(){
        dataItemService2.finishEditionForAll()
//        _editableItem.value = null
    }
}