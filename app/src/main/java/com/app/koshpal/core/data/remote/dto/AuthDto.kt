package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponseDto(
    val userId: String,
    val role: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val isActive: Boolean = true
)
