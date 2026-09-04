package com.app.koshpal.app.domain.usecase.goalusecase

import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result

class DeleteGoalUseCase(private val repository: GoalRepo) {
    suspend operator fun invoke(goal: Goal): Result<Unit, DatabaseCallError> = repository.deleteGoal(goal)
}
