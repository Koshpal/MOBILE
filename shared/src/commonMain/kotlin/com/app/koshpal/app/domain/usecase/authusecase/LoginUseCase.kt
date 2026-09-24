package com.app.koshpal.app.domain.usecase.authusecase

import com.app.koshpal.app.domain.model.User
import com.app.koshpal.app.domain.repository.AuthRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class LoginUseCase(
    private val authRepo: AuthRepo
) {
    suspend operator fun invoke(email: String, password: String): Result<User, NetworkError> {
        return authRepo.login(email, password)
    }
}
