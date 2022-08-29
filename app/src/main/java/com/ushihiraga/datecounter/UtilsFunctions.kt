package com.ushihiraga.datecounter

import android.content.Context
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

fun encodeMapToString(map: Map<String, String>): String {
    return map.entries.joinToString(separator = ";")
}

fun decodeStringToMap(text: String): Map<String, String> {
    return text.split(";").map { it.split("=") }.associate { it.first() to it.last() }
}

fun absoluteDistanceBetweenDates(newDate: LocalDate): Int {
    return abs(ChronoUnit.DAYS.between(newDate, LocalDate.now()).toInt())
}

fun distanceBetweenLabel(context: Context, newDate: LocalDate): String {
    val difference = absoluteDistanceBetweenDates(newDate)
    val nowTime = LocalDate.now()

    return if (newDate.isEqual(nowTime)) {
        context.getString(R.string.event_today)
    } else if (newDate.isBefore(nowTime)) {
        context.resources.getQuantityString(R.plurals.event_past, difference)
    } else {
        //"The event will be in $difference days"
        context.resources.getQuantityString(R.plurals.event_future, difference)
    }
}