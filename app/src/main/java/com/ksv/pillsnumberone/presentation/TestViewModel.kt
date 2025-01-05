package com.ksv.pillsnumberone.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.data.Repository
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.model.DataItemService2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TestViewModel(private val dataItemService2: DataItemService2): ViewModel() {

    private val _actualData = MutableStateFlow<List<DataItem>>(emptyList())
    val actualData = _actualData.asStateFlow()

    private val _setTimeFor = MutableStateFlow<DataItem?>(null)
    val setTimeFor = _setTimeFor.asStateFlow()

    private val _isEditMode = MutableStateFlow<Boolean>(false)
    val isEditMode = _isEditMode.asStateFlow()

    init {
        dataItemService2.actualData.onEach { actualData ->
            Log.d("ksvlog", "TestViewModel Данные из ItemServic'а пришли")
            _actualData.value = actualData
//            Log.d("ksvlog", "TestViewModel service2.actualData.onEach\n$actualData")
        }.launchIn(viewModelScope)

        dataItemService2.isEditMode.onEach {
            _isEditMode.value = it
        }.launchIn(viewModelScope)
    }

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