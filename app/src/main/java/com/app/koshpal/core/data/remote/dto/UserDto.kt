package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val user: AuthResponseDto,
    val accessToken: String,
    val refreshToken: String,
    val redirectUrl: String
)
