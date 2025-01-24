package com.ksv.pillsnumberone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.Period
import com.ksv.pillsnumberone.model.PillsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddPillViewModel(private val pillsService: PillsService): ViewModel() {
    private val _backToMainFragment = MutableStateFlow(false)
    val backToMainFragment = _backToMainFragment.asStateFlow()

    private val _errorNotChecked = MutableStateFlow(false)
    val errorNotChecked = _errorNotChecked.asStateFlow()

    var morningCheck = false
    var noonCheck = false
    var eveningCheck = false
    var title = ""
    var recipe = ""

    fun onAddClick() {
        if (!morningCheck && !noonCheck && !eveningCheck) {
            _errorNotChecked.value = true
        } else {
            val addedPillsList = mutableListOf<DataItem.Pill>()
            if (morningCheck)
                addedPillsList.add(
                    DataItem.Pill(
                        title = title.trim(),
                        recipe = recipe.trim(),
                        period = Period.MORNING
                    )
                )
            if (noonCheck)
                addedPillsList.add(
                    DataItem.Pill(
                        title = title.trim(),
                        recipe = recipe.trim(),
                        period = Period.NOON
                    )
                )
            if (eveningCheck)
                addedPillsList.add(
                    DataItem.Pill(
                        title = title.trim(),
                        recipe = recipe.trim(),
                        period = Period.EVENING
                    )
                )
            pillsService.add(addedPillsList)

            _backToMainFragment.value = true
        }
    }

    fun errorWasMessaged() {
        _errorNotChecked.value = false
    }
}