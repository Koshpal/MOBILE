package com.app.koshpal.core.domain.util

import com.app.koshpal.core.domain.util.Result.Loading
import com.app.koshpal.core.domain.util.Result.Success
import com.app.koshpal.core.domain.util.Result.Error

sealed class Result<out T, out E : CallError> {
    data class Success<out T>(val data: T, val message: String? = null) : Result<T, Nothing>()
    data class Error<out E : CallError>(val error: E, val message: String? = null) : Result<Nothing, E>()
    data class Loading<out T>(val data: T, val message: String? = null) : Result<T, Nothing>()
}

inline fun <T, E: CallError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Error -> Error(error, message)
        is Success -> Success(map(data))
        is Loading -> Loading(map(data), message)
    }
}