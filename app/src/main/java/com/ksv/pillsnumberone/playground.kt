package com.ksv.pillsnumberone

import android.app.PendingIntent.OnFinished
import com.ksv.pillsnumberone.entity.Period

data class Pill(
    val id: Int,
    val title: String,
    val finished: Boolean = false
)

fun main() {

    val noon = Period.NOON
    val ordinal = noon.ordinal
    val arr = Period.entries.toTypedArray()
    val noon2 = arr[ordinal]

    println("$noon $ordinal $noon2")


}

