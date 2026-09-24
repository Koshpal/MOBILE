package com.app.koshpal.core.data.networking

import com.app.koshpal.core.domain.util.NetworkError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.call.body
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.data.remote.dto.ErrorResponse


suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, NetworkError> {
    return when(response.status.value){
        in 200..299 -> {
            try{
                Result.Success(response.body<T>())
            }catch(_: NoTransformationFoundException){
                Result.Error(NetworkError.SERIALIZATION)
            }
        }
        400 -> {
            val msg = try {
                response.body<ErrorResponse>().message?.takeIf { it.isNotBlank() && it != "Bad Request" }
            } catch (_: Exception) {
                null
            }
            Result.Error(NetworkError.BAD_REQUEST, msg ?: "Invalid request. Please check your inputs.")
        }
        401 -> {
            val msg = try {
                response.body<ErrorResponse>().message?.takeIf { it.isNotBlank() && it != "Unauthorized" }
            } catch (_: Exception) {
                null
            }
            Result.Error(NetworkError.INVALID_USER, msg ?: "Session expired. Please log in again.")
        }
        408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
        404 -> {
            val notFoundMessage = try {
                response.body<ErrorResponse>().message
            } catch (_: Exception) {
                "Not Found"
            }
            Result.Error(NetworkError.NOT_FOUND, notFoundMessage)
        }
        429 -> Result.Error(NetworkError.TOO_MANY_REQUEST)
        in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
        else -> Result.Error(NetworkError.UNKNOWN)
    }
}