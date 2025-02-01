package com.ksv.pillsnumberone.ui.home.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.model.PillsService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(private val pillService: PillsService) : ViewModel() {

    private val pillsFromDB: StateFlow<List<DataItem.Pill>> = pillService.pillsList
    private var actualList = listOf<DataItem>()

    private val _actualData = MutableStateFlow<List<DataItem>>(listOf())
    val actualData = _actualData.asStateFlow()

    private var selectedItemId: Long? = null
        set(value) {
            field = value
            _isEditMode.value = field != null
        }

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

//    private val _modifiedPill = MutableStateFlow<DataItem.Pill?>(null)
//    val modifiedPill = _modifiedPill.asStateFlow()

//    private val _setTimeFor = MutableStateFlow<DataItem.Pill?>(null)
//    val setTimeFor = _setTimeFor.asStateFlow()

    private val _showEmptyDataHint = MutableStateFlow(false)
    val showEmptyDataHint = _showEmptyDataHint.asStateFlow()


    private val _state = MutableStateFlow<HomeState>(HomeState.Normal)
    val state = _state.asStateFlow()

    init {
        pillsFromDB.onEach {
            _actualData.value = createDataItemsList(it)
//            includeSelectedPillIntoActualData()
//            _showEmptyDataHint.value = _actualData.value.isEmpty()
////            _state.value = if (it.isEmpty()) HomeState.Empty else HomeState.Normal
//            _state.value = if (it.isEmpty()) HomeState.Empty else HomeState.Normal(_actualData.value)


            actualList = createDataItemsList(it)
//            includeSelectedPillIntoActualData()
//            _state.value = if (it.isEmpty()) HomeState.Empty else HomeState.Normal(actualList)
            if (it.isEmpty()) _state.value = HomeState.Empty
            else {
                _state.value = when(_state.value){
                    HomeState.Empty -> HomeState.Normal
                    else -> _state.value
//                    is HomeState.ModifyItem -> HomeState.ModifyItem((_state.value as HomeState.ModifyItem).id)
//                    is HomeState.Normal -> TODO()
//                    is HomeState.SelectItem -> TODO()
//                    is HomeState.SetTime -> TODO()
                }
            }


        }.launchIn(viewModelScope)
    }


    fun addItem(pill: DataItem.Pill) {
        pillService.add(pill)
    }

    fun moveUp(movedItem: DataItem) {
        if (movedItem is DataItem.Pill)
            pillService.moveUpPill(movedItem)
    }

    fun moveDown(movedItem: DataItem) {
        if (movedItem is DataItem.Pill)
            pillService.moveDownPill(movedItem)
    }

    fun removeItem(removedItem: DataItem) {
        //finishEditMode()
        _state.value = HomeState.Normal
        if (removedItem is DataItem.Pill)
            pillService.remove(removedItem)
    }

    fun itemClick(item: DataItem) {
        if (item is DataItem.Pill) {
////            if (isEditMode.value) {
//            if (_state.value is HomeState.SelectItem) {
////                if (item.id == selectedItemId) {
//                if (_state.value.equals(item.id)) {
//                    //_modifiedPill.value = item
//                    _state.value = HomeState.ModifyItem(item)
//                } else {
//                    //finishEditMode()
//                    _state.value = HomeState.Normal
//                }
//            } else {
//                pillService.switchFinishedState(item)
//            }

            when(_state.value){
                // if now Normal state - change field "finished" on item
                is HomeState.Normal -> pillService.switchFinishedState(item)
                // if some item is Select
                is HomeState.SelectItem -> {
                    val id = (_state.value as HomeState.SelectItem).id
                    if (id == item.id) // click on selected item
                        _state.value = HomeState.ModifyItem(id) // set Modified state
                    else // click on other item
                        _state.value = HomeState.Normal // unSelect item
                }
                else -> {
                    throw IllegalArgumentException("Click on Unable State")
                }
            }



        }
    }

    fun itemLongClick(item: DataItem): Boolean {
//        if (item is DataItem.Pill) {
//            if (item.id != selectedItemId){
//                if (selectedItemId != null) {
//                    unselectItem(selectedItemId!!)
//                }
//                selectedItemId = item.id
//                includeSelectedPillIntoActualData()
//            }
//        }

        val id = (item as DataItem.Pill).id
        _state.value = HomeState.SelectItem(id)

        return true
    }

    fun onTimeClick(item: DataItem) {
        if (item is DataItem.Pill) {
            if (!item.finished && _state.value !is HomeState.SelectItem) {
//                _setTimeFor.value = item
                _state.value = HomeState.SetTime(item)
            }
        }
    }

    fun onAddClick(){
        _state.value = HomeState.AddPills
        viewModelScope.launch {
            delay(1000)
            _state.value = HomeState.Normal
        }
    }

    fun setTimeDialogShowed() {
//        _setTimeFor.value = null
        _state.value = HomeState.Normal
    }

//    fun editDialogShowed() {
//        _modifiedPill.value = null
//    }

    fun editDialogDismiss(id:Long){
        _state.value = HomeState.SelectItem(id)
    }

    fun setTimeDialogDismiss(){
        _state.value = HomeState.Normal
    }

    fun setTimeFor(itemId: Long, time: String?) {
        pillService.setTimeFor(itemId, time)
    }

    fun onApplyClick() {
        //finishEditMode()
        _state.value = HomeState.Normal
    }

//    fun resetModifiedItem() {
//        _modifiedPill.value = null
//    }

    fun modifyPill(pill: DataItem.Pill) {
        pillService.modifyPill(pill)
    }

//    fun getPillByID(id: Long): DataItem.Pill? {
//        val datItem = _actualData.value.firstOrNull { it is DataItem.Pill && it.id == id }
//        datItem?.let {
//            return it as DataItem.Pill
//        }
//        return null
//    }
    fun getPillByID(id: Long): DataItem.Pill? {
        val datItem = actualList.firstOrNull { it is DataItem.Pill && it.id == id }
        datItem?.let {
            return it as DataItem.Pill
        }
        return null
    }

    fun resetPills() {
        pillService.resetPillsToDefaultState()
    }


    private fun includeEditableItemToActualData(data: List<DataItem>): List<DataItem> {
        if (selectedItemId != null) {
            val index = data.indexOfFirst { it is DataItem.Pill && it.id == selectedItemId }
            if (index != -1) {
                val editablePill = (data[index] as DataItem.Pill).copy(editable = true)
                return data.toMutableList().apply {
                    this[index] = editablePill
                }
            }
        }
        return data
    }

//    private fun includeSelectedPillIntoActualData(pill: DataItem.Pill) {
//        val index = _actualData.value.indexOfFirst { it is DataItem.Pill && it.id == pill.id }
//        if (index != -1) {
//            val selectedPill = (_actualData.value[index] as DataItem.Pill).copy(editable = true)
//            _actualData.value = _actualData.value.toMutableList().apply {
//                this[index] = selectedPill
//            }
//        }
//    }

//    private fun includeSelectedPillIntoActualData() {
//        selectedItemId?.let {
//            val index =
//                _actualData.value.indexOfFirst { it is DataItem.Pill && it.id == selectedItemId }
//            if (index != -1) {
//                val selectedPill = (_actualData.value[index] as DataItem.Pill).copy(editable = true)
//                _actualData.value = _actualData.value.toMutableList().apply {
//                    this[index] = selectedPill
//                }
//            }
//        }
//    }

    private fun includeSelectedPillIntoActualData() {
        selectedItemId?.let {
            val index =
                actualList.indexOfFirst { it is DataItem.Pill && it.id == selectedItemId }
            if (index != -1) {
                val selectedPill = (actualList[index] as DataItem.Pill).copy(editable = true)
                actualList = actualList.toMutableList().apply {
                    this[index] = selectedPill
                }
            }
        }
    }

    private fun finishEditMode() {
        //_modifiedPill.value = null
        selectedItemId?.let {
            unselectItem(selectedItemId!!)
            selectedItemId = null
        }
    }

//    private fun unselectItem(itemId: Long) {
//        val index = _actualData.value.indexOfFirst { it is DataItem.Pill && it.id == itemId }
//        if (index != -1) {
//            val unEditablePill = (_actualData.value[index] as DataItem.Pill).copy(editable = false)
//            _actualData.value = _actualData.value.toMutableList().apply {
//                this[index] = unEditablePill
//                this.toList()
//            }
//        }
//    }
    private fun unselectItem(itemId: Long) {
        val index = actualList.indexOfFirst { it is DataItem.Pill && it.id == itemId }
        if (index != -1) {
            val unEditablePill = (actualList[index] as DataItem.Pill).copy(editable = false)
            actualList = actualList.toMutableList().apply {
                this[index] = unEditablePill
                this.toList()
            }
        }
    }

    private fun createDataItemsList(pills: List<DataItem.Pill>): List<DataItem> {
        val dataItemsList = mutableListOf<DataItem>()
        Period.entries.forEach { period ->
            val listByPeriod: MutableList<DataItem> =
                pills.filter { pill -> pill.period == period }
                    .sortedBy { it.position }
                    .toMutableList()

            if (listByPeriod.isNotEmpty()) {
                when (period) {
                    Period.MORNING -> listByPeriod.add(0, MORNING_CAPTION)
                    Period.NOON -> listByPeriod.add(0, NOON_CAPTION)
                    Period.EVENING -> listByPeriod.add(0, EVENING_CAPTION)
                }
                dataItemsList.addAll(listByPeriod)
            }
        }
        return dataItemsList
    }

    companion object {
        private val MORNING_CAPTION = DataItem.PeriodCaption(
            0,
            MyApp.applicationContext.getString(R.string.morning_title),
            Period.MORNING
        )
        private val NOON_CAPTION = DataItem.PeriodCaption(
            1,
            MyApp.applicationContext.getString(R.string.noon_title),
            Period.NOON
        )
        private val EVENING_CAPTION = DataItem.PeriodCaption(
            2,
            MyApp.applicationContext.getString(R.string.evening_title),
            Period.EVENING
        )
    }

}