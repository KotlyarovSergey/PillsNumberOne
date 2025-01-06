package com.ksv.pillsnumberone

import android.app.PendingIntent.OnFinished
import com.ksv.pillsnumberone.entity.Period

data class Pill(
    val id: Int,
    val title: String,
    val finished: Boolean = false,
    var editable: Boolean = false
)

fun main() {
    val list = mutableListOf(Pill(0, "A"), Pill(1, "B"), Pill(2, "C"))
    val list2 = mutableListOf(Pill(0, "A"), Pill(1, "B"), Pill(2, "C"))
    println(list == list2)

    val pill = Pill(3, "D")
    list.add(pill)
    list2.add(pill)
    list2[3].editable = true

    println(list == list2)

}

