package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.model.OnBoardingRequest
import com.app.koshpal.app.domain.model.User
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

interface AuthRepo {
    suspend fun login(email: String, password: String): Result<User, NetworkError>
    suspend fun onBoardingQuestions(selectedOptions: OnBoardingRequest): Result<CommonResponse, NetworkError>
}
