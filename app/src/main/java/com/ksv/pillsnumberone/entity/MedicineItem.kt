package com.ksv.pillsnumberone.entity

data class MedicineItem(
    val title: String,
    val receipt: String,
    val finished: Boolean = false,
    val time: String = "0:00"
)
