package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponseDto(
    val status: String,
    val message: String
)