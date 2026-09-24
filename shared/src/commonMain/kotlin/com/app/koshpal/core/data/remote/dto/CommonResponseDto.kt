package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponseDto(
    val status: String? = "No status",
    val message: String? = "No status or message found in response body."
)
