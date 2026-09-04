package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Auth
import com.app.koshpal.core.data.remote.dto.AuthResponseDto

fun AuthResponseDto.toAuth(): Auth {
    return Auth(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        role = role,
        phone = phone,
        isActive = isActive
    )
}


