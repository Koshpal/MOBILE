package com.app.koshpal.app.domain.usecase.goalusecase

import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.repository.GoalRepo
import kotlinx.coroutines.flow.Flow

class GetAllGoalsUseCase(private val repository: GoalRepo) {
    operator fun invoke(): Flow<List<Goal>> = repository.getAllGoals()
}
