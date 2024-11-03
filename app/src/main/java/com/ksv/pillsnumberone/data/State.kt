package com.ksv.pillsnumberone.data


sealed class State(open val index: Int? = null, open val timess: Timess? = null) {
    data object AddNewItem: State()
    data class EditItem(override val index: Int, override val timess: Timess): State(index, timess)
    data object Normal: State()
}