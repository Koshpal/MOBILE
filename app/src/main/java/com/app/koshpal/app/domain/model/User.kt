package com.app.koshpal.app.domain.model

data class User(
    val user: Auth,
    val accessToken: String,
    val refreshToken: String,
    val redirectUrl: String
)