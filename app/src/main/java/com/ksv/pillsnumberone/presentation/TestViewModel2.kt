package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class TestViewModel2(private val pillsDao: PillsDao): ViewModel() {
    private val pillsFromDB = pillsDao.getAll()

    val actualData = pillsFromDB.map { listPillsDB ->
        DataItemService.makeDataList(listPillsDB.map { it.toPill() })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )



    private val _processedData = MutableStateFlow<List<DataItem>>(emptyList())
    val processedData = _processedData.asStateFlow()



    private val _setTimeFor = MutableStateFlow<DataItem?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()

    private val _editableItem = MutableStateFlow<DataItem?>(null)
    val editableItem = _editableItem.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()




    fun addItem(pill: DataItem.Pill){
        DataItemService(actualData.value, pillsDao).add(pill)
    }

    fun moveUp(movedItem: DataItem) {
        DataItemService(actualData.value, pillsDao).moveUpItem(movedItem)
    }
    fun moveDown(movedItem: DataItem){
        DataItemService(actualData.value, pillsDao).moveDownItem(movedItem)
    }
    fun removeItem(removedItem: DataItem){
        DataItemService(actualData.value, pillsDao).remove(removedItem)
        finishEditMode()
    }
    fun itemClick(item: DataItem){
        DataItemService(actualData.value, pillsDao).switchFinished(item)
    }

    fun itemLongClick(item: DataItem){
        if(!_isEditMode.value) {
            DataItemService(actualData.value, pillsDao).setEditable(item)
            _isEditMode.value = true
        }
//        if(editableItemId == null) {
//            if (item is DataItem.Pill) {
//                editableItemId = item.id
//                val index = dataItemService2.dataItemList.value.indexOfFirst(it == item)
//
//            }
//        }
//
//        dataItemService2.setEditable(item)
    }
    fun setTimeClick(item: DataItem){
        _setTimeFor.value = item
    }
    fun setTimeFinished(){
        _setTimeFor.value = null
    }
    fun setTime(time: String){
        _setTimeFor.value?.let { item ->
            DataItemService(actualData.value, pillsDao).setTimeFor(item, time)
        }
    }
    fun finishEditMode(){
        _isEditMode.value = false
        DataItemService(actualData.value, pillsDao).finishEdition()
    }
}