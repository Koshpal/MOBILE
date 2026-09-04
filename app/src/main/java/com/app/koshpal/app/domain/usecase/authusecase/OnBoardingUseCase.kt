package com.app.koshpal.app.domain.usecase.authusecase

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.model.OnBoardingRequest
import com.app.koshpal.app.domain.repository.AuthRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class OnBoardingUseCase(
    val authRepo: AuthRepo,
) {
    suspend operator fun invoke(selectedOptions: OnBoardingRequest): Result<CommonResponse, NetworkError> {
        return authRepo.onBoardingQuestions(selectedOptions)
    }
}