package com.app.koshpal.app.domain.usecase.goalusecase

import com.app.koshpal.app.data.mapper.toGoal
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.core.data.remote.source.GoalDataSource
import com.app.koshpal.core.domain.util.CallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncGoalsUseCase(
    private val goalRepo: GoalRepo,
    private val goalDataSource: GoalDataSource,
    private val userPreferences: UserPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(): Result<Int, CallError> {
        return withContext(defaultDispatcher) {
            if (userPreferences.isGuestUser.first()) return@withContext Result.Success(0)
            val token = userPreferences.accessToken.first() ?: return@withContext Result.Success(0)

            val localGoals = goalRepo.getAllGoals().first()
            if (localGoals.isNotEmpty() && localGoals.all { it.isSynced }) {
                return@withContext Result.Success(localGoals.size, "Goals already synced")
            }

            return@withContext when (val apiResult = goalDataSource.getGoals(token)) {
                is Result.Success -> {
                    val domainGoals = apiResult.data.map { it.toGoal() }
                    goalRepo.syncGoals(domainGoals)
                    Result.Success(domainGoals.size, "Synced ${domainGoals.size} goals")
                }

                is Result.Error -> {
                    Result.Error(apiResult.error, apiResult.message)
                }

                else -> Result.Success(0)
            }
        }
    }
}
