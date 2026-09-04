package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface GoalRepo {
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun getGoalById(id: String): Result<Goal?, DatabaseCallError>
    suspend fun insertGoal(goal: Goal): Result<Unit, DatabaseCallError>
    suspend fun updateGoal(goal: Goal): Result<Unit, DatabaseCallError>
    suspend fun deleteGoal(goal: Goal): Result<Unit, DatabaseCallError>
    suspend fun deleteGoalsByIds(ids: List<String>): Result<Unit, DatabaseCallError>
    suspend fun deleteAllGoals(): Result<Unit, DatabaseCallError>

    suspend fun syncGoals(goals: List<Goal>): Result<Unit, DatabaseCallError>
    suspend fun syncRemoteGoal(goal: Goal): Result<CommonResponse, NetworkError>
    suspend fun updateRemoteGoal(goal: Goal): Result<CommonResponse, NetworkError>
}
