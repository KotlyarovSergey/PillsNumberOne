package com.ksv.pillsnumberone.ui.home.model

import com.ksv.pillsnumberone.entity.DataItem

sealed interface HomeState {
    data object Empty: HomeState
    data object Normal: HomeState
    class SetTime(val item: DataItem): HomeState
    class ModifyItem(val id: Long): HomeState
    class SelectItem(val id: Long): HomeState
    data object AddPills: HomeState
}