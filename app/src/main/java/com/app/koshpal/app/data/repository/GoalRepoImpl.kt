package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.data.mapper.toCommonResponse
import com.app.koshpal.app.data.mapper.toGoal
import com.app.koshpal.app.data.mapper.toGoalEntity
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.core.data.local.source.GoalLocalDataSource
import com.app.koshpal.core.data.remote.source.GoalDataSource
import com.app.koshpal.app.data.mapper.toGoalDto
import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.core.data.networking.safeDatabaseCall
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.data.remote.dto.GoalDto
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.domain.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GoalRepoImpl(
    private val localDataSource: GoalLocalDataSource,
    private val remoteDataSource: GoalDataSource,
    private val userPreferences: UserPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GoalRepo {
    override fun getAllGoals(): Flow<List<Goal>> {
        return localDataSource.getAllGoals().map { entities ->
            entities.map { it.toGoal() }
        }
    }

    override suspend fun getGoalById(id: String): Result<Goal?, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.getGoalById(id)?.toGoal()
        }
    }

    override suspend fun insertGoal(goal: Goal): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.upsertGoal(goal.toGoalEntity())

          syncRemoteGoal(goal)
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.upsertGoal(goal.toGoalEntity())

          updateRemoteGoal(goal)
        }
    }

    override suspend fun deleteGoal(goal: Goal): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteGoal(goal.toGoalEntity())
        }
    }

    override suspend fun deleteGoalsByIds(ids: List<String>): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteGoalsByIds(ids)
        }
    }

    override suspend fun deleteAllGoals(): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteAllGoals()
        }
    }

    override suspend fun syncGoals(goals: List<Goal>): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            goals.forEach { goal ->
                val existing = localDataSource.getGoalById(goal.id)
                if (existing == null) {
                    localDataSource.upsertGoal(goal.toGoalEntity())
                }
            }
        }
    }

    private suspend fun executeRemoteSync(
        goal: Goal,
        remoteCall: suspend (String, GoalDto) -> Result<CommonResponseDto, NetworkError>
    ): Result<CommonResponse, NetworkError> {
        return withContext(defaultDispatcher){
            if (goal.isSynced) return@withContext Result.Success(CommonResponse("success", "Already synced"))

            val token = userPreferences.accessToken.first()
            if (!userPreferences.isGuestUser.first() && token != null) {
                val dtoResult = remoteCall(token, goal.toGoalDto())
                if (dtoResult is Result.Success) {
                    localDataSource.upsertGoal(goal.toGoalEntity().copy(isSynced = true))
                }
                dtoResult.map { it.toCommonResponse() }
            } else {
                Result.Success(CommonResponse("success", "Offline saved"))
            }
        }
    }

    override suspend fun syncRemoteGoal(goal: Goal) = executeRemoteSync(goal, remoteDataSource::createGoal)
    override suspend fun updateRemoteGoal(goal: Goal) = executeRemoteSync(goal, remoteDataSource::updateGoal)

}
