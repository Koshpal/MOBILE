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
            val badRequestMessage = try {
                response.body<ErrorResponse>().message
            } catch (_: Exception) {
                "Bad Request"
            }
            Result.Error(NetworkError.BAD_REQUEST, badRequestMessage)
        }
        401 -> {
            val unauthorizedMessage = try {
                response.body<ErrorResponse>().message
            } catch (_: Exception) {
                "Unauthorized"
            }
            Result.Error(NetworkError.INVALID_USER, unauthorizedMessage)
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