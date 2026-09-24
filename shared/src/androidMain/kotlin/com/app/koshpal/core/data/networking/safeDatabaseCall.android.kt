package com.app.koshpal.core.data.networking

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.app.koshpal.core.domain.util.DatabaseCallError

actual fun Throwable.toDatabaseCallError(): DatabaseCallError? = when (this) {
    is SQLiteConstraintException -> DatabaseCallError.UNIQUE_CONSTRAINT_VIOLATION
    is SQLiteFullException -> DatabaseCallError.DISK_FULL
    is SQLiteException -> DatabaseCallError.WRITE_ERROR
    else -> null
}
