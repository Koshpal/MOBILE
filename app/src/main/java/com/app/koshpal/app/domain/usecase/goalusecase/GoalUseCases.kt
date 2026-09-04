package com.app.koshpal.app.domain.usecase.goalusecase

data class GoalUseCases(
    val getAllGoals: GetAllGoalsUseCase,
    val getGoalById: GetGoalByIdUseCase,
    val createGoal: CreateGoalUseCase,
    val updateGoal: UpdateGoalUseCase,
    val deleteGoal: DeleteGoalUseCase,
    val deleteGoalsByIds: DeleteGoalsByIdsUseCase,
    val syncGoalsUseCase: SyncGoalsUseCase
)
