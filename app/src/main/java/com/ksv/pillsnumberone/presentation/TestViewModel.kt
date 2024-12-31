package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestViewModel: ViewModel() {
    private var repository: Repository = Repository()

    private val _dataItems = MutableStateFlow<List<DataItem>>(emptyList())
    val actualData = _dataItems.asStateFlow()
    private val dataItemService = DataItemService(_dataItems)
    private val _setTimeFor = MutableStateFlow<DataItem?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()

    init {
        _dataItems.value = repository.getData()
    }

    fun moveUp(movedItem: DataItem) {
        dataItemService.moveUpItem(movedItem)
    }
    fun moveDown(movedItem: DataItem){
        dataItemService.moveDownItem(movedItem)
    }
    fun removeItem(removedItem: DataItem){
        dataItemService.remove(removedItem)
    }
    fun itemClick(item: DataItem){
        dataItemService.click(item)
    }
    fun itemLongClick(item: DataItem){

    }
    fun setTimeClick(item: DataItem){
        _setTimeFor.value = item
    }
    fun setTimeFinished(){
        _setTimeFor.value = null
    }
    fun setTime(time: String){
        _setTimeFor.value?.let { item ->
            dataItemService.setTimeFor(item, time)
        }
    }
}