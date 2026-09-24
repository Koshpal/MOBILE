package com.app.koshpal.app.domain.usecase.goalusecase

import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class GetRemoteGoalsUseCase(
    private val goalRepo: GoalRepo
) {
    suspend operator fun invoke(token: String): Result<List<Goal>, NetworkError> {
        return goalRepo.getRemoteGoals(token)
    }
}
