package com.app.koshpal.app.domain.model

data class Auth(
    val userId: String,
    val role: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val isActive: Boolean
)
