package com.ksv.pillsnumberone.ui.home.model

import com.ksv.pillsnumberone.entity.DataItem

sealed interface HomeState {
    data object Normal: HomeState
    data object Empty: HomeState
    class SetTime(val item: DataItem): HomeState
    class ModifyItem(val item: DataItem): HomeState
    class SelectItem(val item: DataItem): HomeState
}