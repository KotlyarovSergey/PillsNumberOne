package com.ksv.pillsnumberone

import android.annotation.SuppressLint
import android.app.PendingIntent.OnFinished
import androidx.core.app.NotificationCompat.Style
import com.ksv.pillsnumberone.entity.Period
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.SimpleFormatter

data class Pill(
    val id: Int,
    val title: String,
    val finished: Boolean = false,
    var editable: Boolean = false
)

fun main() {
    val calendar = Calendar.getInstance()
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)

    var date = calendar.time
    val formatter = SimpleDateFormat.getInstance()   //("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    println( formatter.format(date))

    var sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    val dateDimeText = sdf.format(date)
    println(dateDimeText)

    date = sdf.parse(dateDimeText)!!
    println(date)


}

