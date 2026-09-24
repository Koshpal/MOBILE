package com.app.koshpal.core.util

import platform.Foundation.NSLog

actual fun logError(message: String, throwable: Throwable?) {
    val fullMessage = if (throwable != null) {
        "ERROR: $message | Throwable: $throwable"
    } else {
        "ERROR: $message"
    }
    NSLog(fullMessage)
}

actual fun logDebug(message: String) {
    NSLog("DEBUG: $message")
}
