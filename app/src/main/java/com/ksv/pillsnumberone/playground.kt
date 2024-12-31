package com.ksv.pillsnumberone

import android.app.PendingIntent.OnFinished

data class Pill(
    val id: Int,
    val title: String,
    val finished: Boolean = false
)

fun main() {
    val list = listOf(Pill(1, "A"), Pill(2, "B"), Pill(3, "C"), Pill(4, "D"))
    println(list)
    val new = list[1].copy(title = "...", finished = true)
    val newList = list.toMutableList().apply {
        this[1] = new
        this.add(new)
    }

    println(list)
    println(newList)
    println(list.javaClass)
    println(list::class.java)

}