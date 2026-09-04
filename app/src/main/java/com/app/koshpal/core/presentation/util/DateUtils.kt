package com.app.koshpal.core.presentation.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val displayDateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
private val legacyFormatters = listOf(
    DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH),
    DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
    DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH),
    DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
)

fun Long.toIso8601String(): String {
    return Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toInstant().toString()
}

fun String.toDisplayDate(): String {
    if (this.isBlank() || this == "Select a date") return "Select a date"

    try {
        val instant = Instant.parse(this)
        return instant.atZone(ZoneId.systemDefault()).format(displayDateFormatter)
    } catch (_: Exception) { }

    for (formatter in legacyFormatters) {
        try {
            val localDate = LocalDate.parse(this, formatter)
            return localDate.format(displayDateFormatter)
        } catch (_: Exception) { }
    }

    return this
}

fun String.parseIsoToLocalDate(): LocalDate? {
    if (this.isBlank() || this == "Select a date") return null
    try {
        return Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDate()
    } catch (_: Exception) { }

    for (formatter in legacyFormatters) {
        try {
            return LocalDate.parse(this, formatter)
        } catch (_: Exception) { }
    }
    return null
}

fun String.truncateTitle(maxLength: Int = 20): String {
    return this.take(maxLength)
}

