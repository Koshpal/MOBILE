package com.app.koshpal.app.domain.usecase.goalusecase

import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result

class GetGoalByIdUseCase(private val repository: GoalRepo) {
    suspend operator fun invoke(id: String): Result<Goal?, DatabaseCallError> = repository.getGoalById(id)
}
