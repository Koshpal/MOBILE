package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.data.mapper.toCommonResponse
import com.app.koshpal.app.data.mapper.toOnBoardingRequestDto
import com.app.koshpal.app.data.mapper.toUser
import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.model.OnBoardingRequest
import com.app.koshpal.app.domain.model.User
import com.app.koshpal.app.domain.repository.AuthRepo
import com.app.koshpal.core.data.remote.dto.AuthRequestDto
import com.app.koshpal.core.data.remote.source.AuthDataSource
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.domain.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val userPreferences: UserPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepo {

    override suspend fun login(email: String, password: String): Result<User, NetworkError> {
        return authDataSource.login(
            AuthRequestDto(email = email, password = password)
        ).map { it.toUser() }
    }

    override suspend fun onBoardingQuestions(selectedOptions: OnBoardingRequest): Result<CommonResponse, NetworkError> {
        return withContext(defaultDispatcher) {
            val token = userPreferences.accessToken.first()
            if(!userPreferences.isGuestUser.first() &&token != null){
                val dtoResult = authDataSource.onBoardingQuestions(selectedOptions.toOnBoardingRequestDto(), accessToken = token)
                dtoResult.map { it.toCommonResponse() }
            }else {
                Result.Success(CommonResponse("success", "Offline saved"))
            }
        }
    }
}
