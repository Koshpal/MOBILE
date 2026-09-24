package com.app.koshpal.app.domain.usecase.goalusecase

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class DeleteRemoteGoalUseCase(
    private val repository: GoalRepo
) {
    suspend operator fun invoke(token: String, goalId: String): Result<CommonResponse, NetworkError> {
        return repository.deleteRemoteGoal(token, goalId)
    }
}
