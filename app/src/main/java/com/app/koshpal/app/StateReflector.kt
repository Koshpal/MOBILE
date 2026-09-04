package com.app.koshpal.app

import com.app.koshpal.core.domain.util.CallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class StateReflector<E : Any>(private val scope: CoroutineScope) {

    private val _events = MutableSharedFlow<E>()
    val events: SharedFlow<E> = _events.asSharedFlow()

    fun emitEvent(event: E) {
        scope.launch {
            _events.emit(event)
        }
    }
}

suspend fun <T, Err : CallError> StateReflector<Events>.handleResult(
    result: Result<T, Err>,
    onSuccess: suspend (T) -> Unit = {}
) {
    when (result) {
        is Result.Success -> {
            if (!result.message.isNullOrBlank()) {
                emitEvent(Events.Success(result.message))
            }
            onSuccess(result.data)
        }
        is Result.Error -> {
            val errorMessage = if (result.error is com.app.koshpal.core.domain.util.NetworkError && 
                result.error == com.app.koshpal.core.domain.util.NetworkError.NO_INTERNET) {
                "You are offline. Turn on internet to sync your data."
            } else {
                result.message
            }
            Timber.e("Error: ${result.error} | Message: $errorMessage")
            emitEvent(Events.Error(result.error, errorMessage))
        }
        else -> {}
    }
}
