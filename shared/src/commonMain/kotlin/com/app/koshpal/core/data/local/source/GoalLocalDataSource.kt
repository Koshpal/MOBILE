package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.GoalEntity
import kotlinx.coroutines.flow.Flow

interface GoalLocalDataSource {
    fun getAllGoals(): Flow<List<GoalEntity>>
    suspend fun getGoalById(id: String): GoalEntity?
    suspend fun upsertGoal(goal: GoalEntity)
    suspend fun deleteGoal(goal: GoalEntity)
    suspend fun deleteGoalsByIds(ids: List<String>)
    suspend fun deleteAllGoals()
}
