package com.ksv.pillsnumberone.entity

data class MedicineItem(
    val title: String,
    val recipe: String,
    var finished: Boolean = false,
    var time: String = "0:00"
)
