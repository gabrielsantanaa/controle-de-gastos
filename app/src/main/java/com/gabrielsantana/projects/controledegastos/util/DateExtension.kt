package com.gabrielsantana.projects.controledegastos.util

import java.text.SimpleDateFormat
import java.util.*

private const val ONE_YEAR_IN_MILLISECONDS = 31556952000L
fun Date.formatDate(): String {
    val dateFormat = SimpleDateFormat.getDateInstance()
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(this)
}

fun Date.formatMonthAndYear(locale: Locale): String {
    val dateFormat = SimpleDateFormat("MMM yyyy", locale)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(this)
}

fun Date.formatYear(locale: Locale): String {
    val dateFormat = SimpleDateFormat("yyyy", locale)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(this)
}

fun Date.decreaseYear(): Date {
    time -= ONE_YEAR_IN_MILLISECONDS
    return this
}

fun Date.increaseYear(): Date {
    time += ONE_YEAR_IN_MILLISECONDS
    return this
}
