package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestViewModel(private val dataItemService2: DataItemService2): ViewModel() {

    val actualData = dataItemService2.dataItemList
    val isEditMode = dataItemService2.isEditMode

    private val _setTimeFor = MutableStateFlow<DataItem?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()



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
        finishEdition()
    }
    fun itemClick(item: DataItem){
        dataItemService2.onClick(item)
    }
    fun itemLongClick(item: DataItem){
        dataItemService2.longClick(item)
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
    fun finishEdition(){
        dataItemService2.finishEditionForAll()
    }
}