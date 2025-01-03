package com.ksv.pillsnumberone.entity

sealed class DataItem {
    data class Caption(
        val id: Int,
        val caption: String,
        val period: Period
    ): DataItem()
    data class Pill(
        val id: Long = 0,
        val title: String,
        val recipe: String,
        val period: Period,
        val position: Int = 0,
        val time: String = "0:00",
        val finished: Boolean = false,
        val editable: Boolean = false,
    ): DataItem()
}