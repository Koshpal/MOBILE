package com.app.koshpal.core.data.networking

import android.util.Base64

object SecureBaseUrl {
    private const val ENCODED_URL = "aHR0cHM6Ly9hcGkua29zaHBhbC5jb20vYXBpL3YxLw=="

    val value: String by lazy {
        String(Base64.decode(ENCODED_URL, Base64.DEFAULT), Charsets.UTF_8)
    }
}
