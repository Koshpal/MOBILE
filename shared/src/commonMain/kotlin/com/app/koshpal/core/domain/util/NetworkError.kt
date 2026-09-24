package com.app.koshpal.core.domain.util

enum class NetworkError: CallError {
    REQUEST_TIMEOUT,
    TOO_MANY_REQUEST,
    NO_INTERNET,
    SERVER_ERROR,
    SERIALIZATION,
    TLS_ERROR,
    NOT_FOUND,
    UNKNOWN,
    INVALID_USER,
    BAD_REQUEST
}
