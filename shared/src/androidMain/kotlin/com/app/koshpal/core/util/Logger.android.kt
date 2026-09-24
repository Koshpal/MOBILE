package com.app.koshpal.core.util

import timber.log.Timber

actual fun logError(message: String, throwable: Throwable?) {
    if (throwable != null) {
        Timber.e(throwable, message)
    } else {
        Timber.e(message)
    }
}

actual fun logDebug(message: String) {
    Timber.d(message)
}
