package com.ksv.pillsnumberone.entity

sealed class DataItem {
    data class PeriodCaption(
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
        val time: String? = null,
        val finished: Boolean = false,
    ): DataItem(){
        fun toPillDB(): PillToDB{
            return PillToDB(
                id = id,
                title = title,
                recipe = recipe,
                period = period.ordinal,
                position = position,
                time = time,
                finished = finished,
            )
        }
    }
}

