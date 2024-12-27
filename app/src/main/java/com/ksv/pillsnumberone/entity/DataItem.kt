package com.ksv.pillsnumberone.entity

sealed class DataItem {
    class Caption(
        val caption: String,
        val period: Period
    ): DataItem()

    class Pill(
        val title: String,
        val recipe: String,
        val period: Period,
        val position: Int,
        val time: String = "0:00",
        val finished: Boolean = false,
        val editable: Boolean = false,
    ): DataItem()
}