package com.ksv.pillsnumberone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.model.DataItemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddPillViewModel(private val dataItemService: DataItemService): ViewModel() {
    private val _backToMainFragment = MutableStateFlow(false)
    val backToMainFragment = _backToMainFragment.asStateFlow()

    private val _errorNotChecked = MutableStateFlow(false)
    val errorNotChecked = _errorNotChecked.asStateFlow()

    var morningCheck = false
    var noonCheck = false
    var eveningCheck = false
    var title = ""
    var recipe = ""

    fun onAddClick(){
        if(!morningCheck && !noonCheck && !eveningCheck){
            _errorNotChecked.value = true
        } else {
            if(morningCheck) dataItemService.add(DataItem.Pill(title = title, recipe = recipe, period = Period.MORNING))
            if(noonCheck) dataItemService.add(DataItem.Pill(title = title, recipe = recipe, period = Period.NOON))
            if(eveningCheck) dataItemService.add(DataItem.Pill(title = title, recipe = recipe, period = Period.EVENING))
            _backToMainFragment.value = true
        }
    }

    fun errorWasMessaged(){
        _errorNotChecked.value = false
    }
}