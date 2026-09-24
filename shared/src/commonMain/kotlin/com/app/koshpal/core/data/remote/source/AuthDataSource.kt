package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.remote.dto.AuthRequestDto
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.data.remote.dto.OnBoardingRequestDto
import com.app.koshpal.core.data.remote.dto.RefreshTokenRequestDto
import com.app.koshpal.core.data.remote.dto.UserDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

interface AuthDataSource {
    suspend fun login(request: AuthRequestDto): Result<UserDto, NetworkError>
    suspend fun onRefreshToken(request: RefreshTokenRequestDto): Result<UserDto, NetworkError>
    suspend fun onBoardingQuestions(selectedOptions: OnBoardingRequestDto, accessToken: String): Result<CommonResponseDto, NetworkError>
}
