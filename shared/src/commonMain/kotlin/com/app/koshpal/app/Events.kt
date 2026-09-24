package com.app.koshpal.app

import com.app.koshpal.core.domain.util.CallError

sealed interface Events {
    data class Success(val message: String?): Events
    data class Error(val error: CallError, val message: String?): Events
    data class LoggedOut(val message: String?): Events
}
