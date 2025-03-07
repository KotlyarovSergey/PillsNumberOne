package com.ksv.pillsnumberone.entity.old

import kotlinx.serialization.Serializable

@Serializable
data class MedicineItem(
    val title: String,
    val recipe: String,
    var finished: Boolean = false,
    var time: String = "0:00",
    var editable: Boolean = false
)