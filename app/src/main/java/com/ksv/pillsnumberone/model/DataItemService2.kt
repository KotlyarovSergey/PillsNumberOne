package com.ksv.pillsnumberone.model

import android.util.Log
import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DataItemService2(private val pillsDao: PillsDao) {
    private val pillsDB = pillsDao.getAll()

    private val _actualData = MutableStateFlow<List<DataItem>>(emptyList())
    val actualData = _actualData.asStateFlow()

    private val _isEditMode = MutableStateFlow<Boolean>(false)
    val isEditMode = _isEditMode.asStateFlow()

    init {
        pillsDB.onEach { pillsDBList ->
            Log.d("ksvlog", "DataItemService2 Данные из ДБ пришли")
            val listOfPills = pillsDBList.map { it.toPill() }
            val rightList = makeDataList(listOfPills)
            _actualData.value = rightList
//            _actualData.emit(rightList)
//            _actualData.update { rightList }
            _isEditMode.value = checkEdit()
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    fun add(addedPill: DataItem.Pill) {
        val pillWithRightPosition = setPosition(addedPill)
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.insert(pillWithRightPosition.toPillDB())
        }
    }

    fun remove(deletedItem: DataItem) {
        if (deletedItem is DataItem.Pill) {
            CoroutineScope(Dispatchers.Default).launch {
                pillsDao.delete(deletedItem.toPillDB())
            }
        }
    }

    fun moveUpItem(movedItem: DataItem) {
        val indexOfMoved = _actualData.value.indexOf(movedItem)
        if (indexOfMoved > 0) { // исключаем 0, потому что 0 должен быть у Caption'a
            if(movedItem is DataItem.Pill) {
                if (canBeMoveUp(indexOfMoved)) {
                    val previousItem = _actualData.value[indexOfMoved - 1] as DataItem.Pill
                    val newMovedItem = movedItem.copy(position = previousItem.position)
                    val newPreviousItem = previousItem.copy(position = movedItem.position)
                    CoroutineScope(Dispatchers.Default).launch{
                        pillsDao.update(newMovedItem.toPillDB())
                        pillsDao.update(newPreviousItem.toPillDB())
                    }

//                    val previousItem = _actualData.value[indexOfMoved - 1] as DataItem.Pill
//                    movedItem.position--
//                    previousItem.position++
//                    CoroutineScope(Dispatchers.Default).launch {
//                        pillsDao.update(movedItem.toPillDB())
//                        pillsDao.update(previousItem.toPillDB())
//                    }
                }
            }
        }
    }

    fun moveDownItem(movedItem: DataItem) {
        val indexOfMoved = _actualData.value.indexOf(movedItem)
        if (indexOfMoved > 0) {
            if(movedItem is DataItem.Pill) {
                if (canBeMoveDown(indexOfMoved)) {
                    val nextItem = _actualData.value[indexOfMoved + 1] as DataItem.Pill
                    val updatedNextItem = nextItem.copy(position = movedItem.position)
                    val updatedMovedItem = movedItem.copy(position = nextItem.position)
                    CoroutineScope(Dispatchers.Default).launch{
                        pillsDao.update(updatedMovedItem.toPillDB())
                        pillsDao.update(updatedNextItem.toPillDB())
                    }
                }
            }
        }
    }

    fun onClick(clickedItem: DataItem) {
//        if (clickedItem is DataItem.Pill) {
//            clickedItem.finished = !clickedItem.finished
//            CoroutineScope(Dispatchers.Default).launch {
//                pillsDao.update(clickedItem.toPillDB())
//            }
//        }

        if (clickedItem is DataItem.Pill) {
            val updatedItem = clickedItem.copy(finished = !clickedItem.finished)
            CoroutineScope(Dispatchers.Default).launch {
                pillsDao.update(updatedItem.toPillDB())
            }
        }
    }

    fun longClick(clickedItem: DataItem) {
        val indexOfClicked = _actualData.value.indexOf(clickedItem)
        if (indexOfClicked > 0) {
            if (clickedItem is DataItem.Pill) {
                if (itCanBeEdit(indexOfClicked)) {
                    val newItem = clickedItem.copy(editable = true)
                    CoroutineScope(Dispatchers.Default).launch {
                        pillsDao.update(newItem.toPillDB())
                    }
                }
            }
        }
    }

    fun finishEditionForAll() {
        _actualData.value.forEach {
            if (it is DataItem.Pill && it.editable){
                val newItem = it.copy(editable = false)
                CoroutineScope(Dispatchers.Default).launch {
                    pillsDao.update(newItem.toPillDB())
                }
            }
        }
        // _isEditMode.value = false
    }




    private fun makeDataList(pills: List<DataItem.Pill>): List<DataItem> {
        val morningPills: MutableList<DataItem> = pills
            .filter { pill -> pill.period == Period.MORNING }
            .sortedBy { it.position }
            .toMutableList()
        if (morningPills.isNotEmpty()) morningPills.add(0, MORNING_CAPTION)

        val noonPills: MutableList<DataItem> = pills
            .filter { it.period == Period.NOON }
            .sortedBy { it.position }
            .toMutableList()
        if (noonPills.isNotEmpty()) noonPills.add(0, NOON_CAPTION)

        val eveningPills: MutableList<DataItem> = pills
            .filter { it.period == Period.EVENING }
            .sortedBy { it.position }
            .toMutableList()
        if (eveningPills.isNotEmpty()) eveningPills.add(0, EVENING_CAPTION)

        return mutableListOf<DataItem>().apply {
            addAll(morningPills)
            addAll(noonPills)
            addAll(eveningPills)
            toList()
        }
    }

    private fun checkEdit(): Boolean {
        _actualData.value.forEach {
            if(it is DataItem.Pill){
                if (it.editable)
                    return true
            }
        }
        return false
    }

    private fun setPosition(pill: DataItem.Pill): DataItem.Pill{
        val pillsByPeriod = _actualData.value
            .filter { it is DataItem.Pill && it.period == pill.period }
        val lastPosition = pillsByPeriod.lastIndex + 1
        return pill.copy(position = lastPosition)
//        pill.position = pillsByPeriod.lastIndex + 1
//        return pill
    }

    private fun itCanBeEdit(checkedIndex: Int): Boolean {
        _actualData.value.forEachIndexed { index, item ->
            if (item is DataItem.Pill) {
                if (item.editable && index != checkedIndex)
                    return false
            }
        }
        return true
    }

    private fun canNotBeMoveUp(indexOfMoved: Int): Boolean {
        val previousItem = _actualData.value[indexOfMoved - 1]
        return previousItem is DataItem.Caption
    }

    private fun canBeMoveUp(indexOfMoved: Int):Boolean{
        val previousItem = _actualData.value[indexOfMoved - 1]
        return previousItem !is DataItem.Caption
    }

    private fun canNotBeMoveDown(indexOfMoved: Int): Boolean {
        if (indexOfMoved == _actualData.value.lastIndex) return true
        val nextItem = _actualData.value[indexOfMoved + 1]
        return nextItem is DataItem.Caption
    }

    private fun canBeMoveDown(indexOfMoved: Int): Boolean {
        if (indexOfMoved == _actualData.value.lastIndex) return false
        val nextItem = _actualData.value[indexOfMoved + 1]
        return nextItem !is DataItem.Caption
    }



    companion object {
        // private val MORNING_CAPTION = DataItem.Caption(0, "Утро", Period.MORNING)
        // private val NOON_CAPTION = DataItem.Caption(1,"Обед", Period.NOON)
        // private val EVENING_CAPTION = DataItem.Caption(2, "Вечер", Period.EVENING)
        private val MORNING_CAPTION = DataItem.Caption(
            0,
            MyApp.applicationContext.getString(R.string.morning_title),
            Period.MORNING
        )
        private val NOON_CAPTION = DataItem.Caption(
            1,
            MyApp.applicationContext.getString(R.string.noon_title),
            Period.NOON
        )
        private val EVENING_CAPTION = DataItem.Caption(
            2,
            MyApp.applicationContext.getString(R.string.evening_title),
            Period.EVENING
        )
    }
}