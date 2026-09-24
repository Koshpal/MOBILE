package com.app.koshpal.core.data.networking

import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

expect fun Throwable.toDatabaseCallError(): DatabaseCallError?

suspend inline fun <T> safeDatabaseCall(
    crossinline execute: suspend () -> T
): Result<T, DatabaseCallError> {
    return try {
        val result = execute()
        Result.Success(result)
    } catch (e: Throwable) {
        currentCoroutineContext().ensureActive()
        val errorCategory = e.toDatabaseCallError()
        if (errorCategory != null) {
            Result.Error(errorCategory)
        } else {
            Result.Error(DatabaseCallError.UNKNOWN, message = e.message)
        }
    }
}
