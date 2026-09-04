package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.User
import com.app.koshpal.core.data.remote.dto.UserDto


fun UserDto.toUser(): User {
    return User(
        user = user.toAuth(),
        accessToken = accessToken,
        refreshToken = refreshToken,
        redirectUrl = redirectUrl
    )
}