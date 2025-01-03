package com.ksv.pillsnumberone.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TestViewModel: ViewModel() {
    private var repository: Repository = Repository()

    private val _dataItems = MutableStateFlow<List<DataItem>>(emptyList())
    val actualData = _dataItems.asStateFlow()

    private val dataItemService = DataItemService(_dataItems)
    private val _setTimeFor = MutableStateFlow<DataItem?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()

    private val _isEditMode = MutableStateFlow<Boolean>(false)
    val isEditMode = _isEditMode.asStateFlow()

    init {
        Log.d("ksvlog", "TestViewModel init")
        //_dataItems.value = repository.getData()
        val pills = repository.getPills()
        _dataItems.value = dataItemService.makeDataList(pills)

        dataItemService.isEditMode.onEach {
            _isEditMode.value = it
        }.launchIn(viewModelScope)
    }

    fun moveUp(movedItem: DataItem) {
        dataItemService.moveUpItem(movedItem)
    }
    fun moveDown(movedItem: DataItem){
        dataItemService.moveDownItem(movedItem)
    }
    fun removeItem(removedItem: DataItem){
        dataItemService.remove(removedItem)
        finishEdition()
    }
    fun itemClick(item: DataItem){
        dataItemService.click(item)
    }
    fun itemLongClick(item: DataItem){
        dataItemService.longClick(item)
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
    fun finishEdition(){
        dataItemService.finishEditionForAll()
    }
}