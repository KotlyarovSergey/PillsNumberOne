package com.ksv.pillsnumberone.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pills")
class PillToDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pill_id") val id: Long = 0,
    val title: String,
    val recipe: String,
    val period: Int,
    val position: Int = 0,
    val time: String? = null,
    val finished: Boolean
){
    fun toPill(): DataItem.Pill{
        val periodEnum = try {
            Period.entries[period]
        } catch (exception: Exception){
            Period.MORNING
        }

        return DataItem.Pill(
            id = id,
            title = title,
            recipe = recipe,
            period = periodEnum,
            position = position,
            time = time,
            finished = finished
        )
    }
}