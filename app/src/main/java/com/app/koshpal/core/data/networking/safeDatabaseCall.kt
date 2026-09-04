package com.app.koshpal.core.data.networking

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <T> safeDatabaseCall(
    crossinline execute: suspend () -> T
): Result<T, DatabaseCallError> {
    return try {
        val result = execute()
        Result.Success(result)
    } catch (_: SQLiteConstraintException) {
        Result.Error(DatabaseCallError.UNIQUE_CONSTRAINT_VIOLATION)
    } catch (_: SQLiteFullException) {
        Result.Error(DatabaseCallError.DISK_FULL)
    } catch (_: SQLiteException) {
        Result.Error(DatabaseCallError.WRITE_ERROR)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Result.Error(DatabaseCallError.UNKNOWN, message = e.localizedMessage)
    }
}