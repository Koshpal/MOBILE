package com.app.koshpal.core.data.networking

import com.app.koshpal.core.domain.util.NetworkError
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.currentCoroutineContext


import javax.net.ssl.SSLException
import timber.log.Timber

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        execute()
    } catch (_: UnresolvedAddressException) {
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        Timber.e(e, "Serialization Error")
        return Result.Error(NetworkError.SERIALIZATION, e.message)
    } catch (e: SSLException) {
        Timber.e(e, "SSL/TLS Handshake Error")
        return Result.Error(NetworkError.TLS_ERROR, "Security handshake failed (Check device time)")
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Timber.e(e, "SafeCall Unknown Error")
        return Result.Error(NetworkError.UNKNOWN)
    }

    return responseToResult(response)
}
