package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.networking.safeCall
import com.app.koshpal.core.data.networking.constructUrl
import com.app.koshpal.core.data.remote.dto.AuthRequestDto
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.data.remote.dto.OnBoardingRequestDto
import com.app.koshpal.core.data.remote.dto.UserDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RemoteAuthDataSource(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun login(request: AuthRequestDto): Result<UserDto, NetworkError> {
        return safeCall<UserDto> {
            httpClient.post(
                urlString = constructUrl("/auth/login")
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override suspend fun onBoardingQuestions(selectedOptions: OnBoardingRequestDto, accessToken: String): Result<CommonResponseDto, NetworkError> {
        return safeCall<CommonResponseDto> {
            httpClient.post(
                urlString = constructUrl("/onboarding")
            ) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(selectedOptions)
            }
        }
    }

}
