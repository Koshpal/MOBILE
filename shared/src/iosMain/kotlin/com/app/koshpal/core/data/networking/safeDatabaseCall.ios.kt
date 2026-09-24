package com.app.koshpal.core.data.networking

import androidx.sqlite.SQLiteException
import com.app.koshpal.core.domain.util.DatabaseCallError

actual fun Throwable.toDatabaseCallError(): DatabaseCallError? = when (this) {
    is SQLiteException -> {
        val msg = message?.lowercase() ?: ""
        when {
            msg.contains("unique") || msg.contains("constraint") -> DatabaseCallError.UNIQUE_CONSTRAINT_VIOLATION
            msg.contains("full") || msg.contains("disk") -> DatabaseCallError.DISK_FULL
            else -> DatabaseCallError.WRITE_ERROR
        }
    }
    else -> null
}
