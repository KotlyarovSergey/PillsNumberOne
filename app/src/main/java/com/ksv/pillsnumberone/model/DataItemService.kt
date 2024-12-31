package com.ksv.pillsnumberone.model

import android.util.Log
import com.ksv.pillsnumberone.entity.DataItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Collections

class DataItemService(private val items: MutableStateFlow<List<DataItem>>) {
//class DataItemService(private val items: List<DataItem>) {

//    fun moveUpItem(movedItem: DataItem): List<DataItem> {
//        val indexOfMoved = items.indexOf(movedItem)
//        if (indexOfMoved > 0) { // исключаем 0 потому, что 0 должен быть у Caption'a
//            if (canNotBeMoveUp(indexOfMoved)) return items
//            val newList = items.toMutableList()
//            Collections.swap(newList, indexOfMoved, indexOfMoved - 1)
//            return newList.toList()
//        }
//        return items
//    }
//
//    private fun canNotBeMoveUp(indexOfMoved: Int): Boolean {
//        val previousItem = items[indexOfMoved - 1]
//        return previousItem is DataItem.Caption
//    }
//
//    fun select(selectedItem: DataItem): List<DataItem> {
//        val indexOfSelected = items.indexOf(selectedItem)
//        if (indexOfSelected > 0) {
//            val item = items[indexOfSelected]
//            if (item is DataItem.Pill) {
//                val finished = item.finished
//                return items.toMutableList().apply {
//                    val old = selectedItem as DataItem.Pill
//                    val new = old.copy(finished = !finished)
//                    this[indexOfSelected] = new
//                    Log.d("ksvlog", "new: $new")
//                }
//            }
//        }
//        return items
//    }
//
//    fun setTimeFor(item: DataItem, time: String): List<DataItem>{
//        if (item is DataItem.Pill){
//           val indexOfItem = items.indexOf(item)
//           if (indexOfItem > 0){
//               return items.toMutableList().apply {
//                   val new = item.copy(time = time)
//                   this[indexOfItem] = new
//               }
//           }
//        }
//        return items
//    }


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

    fun click(clickedItem: DataItem){
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

    fun setTimeFor(item: DataItem, time: String){
        if (item is DataItem.Pill){
           val indexOfItem = items.value.indexOf(item)
           if (indexOfItem > 0){
               items.value = items.value.toMutableList().apply {
                   val new = item.copy(time = time)
                   this[indexOfItem] = new
               }
           }
        }
    }

}
