package com.app.koshpal.core.presentation.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val MONTH_NAMES = arrayOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

private val MONTH_ABBRS = arrayOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

fun Long.toIso8601String(): String {
    return Instant.fromEpochMilliseconds(this).toString()
}

fun String.toDisplayDate(): String {
    if (this.isBlank() || this == "Select a date") return "Select a date"

    try {
        val instant = Instant.parse(this)
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val month = MONTH_NAMES[date.monthNumber - 1]
        val year = date.year
        return "$day $month $year"
    } catch (_: Exception) { }

    val date = this.parseIsoToLocalDate() ?: return this
    val day = date.dayOfMonth.toString().padStart(2, '0')
    val month = MONTH_NAMES[date.monthNumber - 1]
    val year = date.year
    return "$day $month $year"
}

fun String.parseIsoToLocalDate(): LocalDate? {
    if (this.isBlank() || this == "Select a date") return null
    try {
        val instant = Instant.parse(this)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    } catch (_: Exception) { }

    try {
        return LocalDate.parse(this)
    } catch (_: Exception) { }

    val parts = this.trim().split(" ", "-", "/")
    if (parts.size == 3) {
        val day = parts[0].toIntOrNull()
        val year = parts[2].toIntOrNull()
        if (day != null && year != null) {
            val monthStr = parts[1]
            val monthIndex = monthStr.toIntOrNull()?.let { it - 1 }
                ?: MONTH_ABBRS.indexOfFirst { it.equals(monthStr, ignoreCase = true) }.takeIf { it >= 0 }
                ?: MONTH_NAMES.indexOfFirst { it.equals(monthStr, ignoreCase = true) }.takeIf { it >= 0 }
            if (monthIndex != null && monthIndex in 0..11) {
                return LocalDate(year, monthIndex + 1, day)
            }
        }
    }

    return null
}

fun String.truncateTitle(maxLength: Int = 20): String {
    return this.take(maxLength)
}
