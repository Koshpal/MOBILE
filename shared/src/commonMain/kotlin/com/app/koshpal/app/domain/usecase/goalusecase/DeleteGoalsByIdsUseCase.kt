package com.app.koshpal.app.domain.usecase.goalusecase

import com.app.koshpal.app.domain.repository.GoalRepo

class DeleteGoalsByIdsUseCase(private val repository: GoalRepo) {
    suspend operator fun invoke(ids: List<String>) {
        repository.deleteGoalsByIds(ids)
    }
}
