package com.app.koshpal.core.data.networking

fun constructUrl(
    url: String,
): String {
    val baseUrl = SecureBaseUrl.value
    return when {
        url.contains(baseUrl) -> url
        url.startsWith("/") -> baseUrl + url.drop(1)
        else -> baseUrl + url
    }
}
