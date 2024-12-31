package com.ksv.pillsnumberone.model

import android.util.Log
import com.ksv.pillsnumberone.entity.DataItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Collections

class DataItemService(private val items: MutableStateFlow<List<DataItem>>) {
//class DataItemService(private val items: List<DataItem>) {
    private val _isEditMode = MutableStateFlow<Boolean>(false)
    val isEditMode = _isEditMode.asStateFlow()



    fun moveUpItem(movedItem: DataItem) {
        val indexOfMoved = items.value.indexOf(movedItem)
        if (indexOfMoved > 0) { // исключаем 0 потому, что 0 должен быть у Caption'a
            if (canNotBeMoveUp(indexOfMoved)) return
            val newList = items.value.toMutableList()
            Collections.swap(newList, indexOfMoved, indexOfMoved - 1)
            items.value = newList.toList()
        }
    }

    fun moveDownItem(movedItem: DataItem) {
        val indexOfMoved = items.value.indexOf(movedItem)
        if (indexOfMoved > 0) {
            if (canNotBeMoveDown(indexOfMoved)) return
            val newList = items.value.toMutableList()
            Collections.swap(newList, indexOfMoved, indexOfMoved + 1)
            items.value = newList.toList()
        }
    }

    private fun canNotBeMoveUp(indexOfMoved: Int): Boolean {
        val previousItem = items.value[indexOfMoved - 1]
        return previousItem is DataItem.Caption
    }

    private fun canNotBeMoveDown(indexOfMoved: Int): Boolean {
        if (indexOfMoved == items.value.lastIndex) return true
        val nextItem = items.value[indexOfMoved + 1]
        return nextItem is DataItem.Caption
    }

    fun remove(deletedItem: DataItem) {
        val indexOfDeleted = items.value.indexOf(deletedItem)
        if (indexOfDeleted > 0) {
            val newList = items.value.toMutableList().apply {
                removeAt(indexOfDeleted)
            }
            items.value = newList.toList()
        }
    }

    fun click(clickedItem: DataItem) {
        val indexOfClicked = items.value.indexOf(clickedItem)
        if (indexOfClicked > 0) {
            if (clickedItem is DataItem.Pill) {
                val finished = clickedItem.finished
                items.value = items.value.toMutableList().apply {
                    val newItem = clickedItem.copy(finished = !finished)
                    this[indexOfClicked] = newItem
                }
            }
        }
    }

    fun longClick(clickedItem: DataItem) {
        val indexOfClicked = items.value.indexOf(clickedItem)
        if (indexOfClicked > 0) {
            if (clickedItem is DataItem.Pill) {
                if (canBeEdit(indexOfClicked)) {
                    if(!clickedItem.editable) {
                        items.value = items.value.toMutableList().apply {
                            val newItem = clickedItem.copy(editable = true)
                            this[indexOfClicked] = newItem
                        }
                        _isEditMode.value = true
                    }
                }
            }
        }
    }

    fun finishEditionForAll(){
        items.value = items.value.toMutableList().apply {
            this.forEachIndexed() { index, item ->
                if(item is DataItem.Pill) {
                    val newItem = item.copy(editable = false)
                    this[index] = newItem
                }
            }
        }
        _isEditMode.value = false
    }

    private fun canBeEdit(checkedIndex: Int): Boolean {
        items.value.forEachIndexed() { index, item ->
            if (item is DataItem.Pill) {
                if (item.editable && index != checkedIndex)
                    return false
            }
        }
        return true
    }

    fun setTimeFor(item: DataItem, time: String) {
        if (item is DataItem.Pill) {
            val indexOfItem = items.value.indexOf(item)
            if (indexOfItem > 0) {
                items.value = items.value.toMutableList().apply {
                    val new = item.copy(time = time)
                    this[indexOfItem] = new
                }
            }
        }
    }

}
