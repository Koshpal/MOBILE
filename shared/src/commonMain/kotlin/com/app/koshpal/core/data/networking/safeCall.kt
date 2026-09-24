package com.app.koshpal.core.data.networking

import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.util.logError
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        execute()
    } catch (_: UnresolvedAddressException) {
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        logError("Serialization Error", e)
        return Result.Error(NetworkError.SERIALIZATION, e.message)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        logError("SafeCall Unknown Error", e)
        return Result.Error(NetworkError.UNKNOWN)
    }

    return responseToResult(response)
}
