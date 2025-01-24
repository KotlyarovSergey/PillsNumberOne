package com.ksv.pillsnumberone.ui.home.model

import com.ksv.pillsnumberone.entity.DataItem
import kotlinx.coroutines.flow.StateFlow

sealed interface HomeState {
    data object Empty: HomeState
    data object Normal: HomeState
    class SelectAt(val item: DataItem): HomeState
    class ModifyAt(val item: DataItem): HomeState
    class SetTimeAt(val item: DataItem): HomeState
}