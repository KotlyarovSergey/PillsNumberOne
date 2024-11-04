package com.ksv.pillsnumberone.data


sealed class State(open val index: Int? = null, open val eatingTime: EatingTime? = null) {
    data object AddNewItem: State()
    data class EditItem(override val index: Int, override val eatingTime: EatingTime): State(index, eatingTime)
    data object Normal: State()
}