package com.app.koshpal.core.presentation.util

import android.content.Context
import com.app.koshpal.R
import com.app.koshpal.core.domain.util.NetworkError

fun NetworkError.toString(context: Context): String {
     val resId = when (this) {
            NetworkError.REQUEST_TIMEOUT -> R.string.error_request_timeout
            NetworkError.TOO_MANY_REQUEST -> R.string.error_too_many_requests
            NetworkError.NO_INTERNET -> R.string.error_internet
            NetworkError.SERVER_ERROR -> R.string.error_unknown
            NetworkError.SERIALIZATION -> R.string.error_serialization
            NetworkError.TLS_ERROR -> R.string.error_unknown
            NetworkError.UNKNOWN -> R.string.error_unknown
            NetworkError.INVALID_USER -> R.string.error_invalid_user
            NetworkError.BAD_REQUEST -> R.string.error_unknown
            NetworkError.NOT_FOUND -> R.string.error_not_found
     }
     return context.getString(resId)
}