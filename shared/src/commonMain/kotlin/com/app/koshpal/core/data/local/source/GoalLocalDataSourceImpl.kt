package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.GoalEntity
import com.app.koshpal.core.data.local.dao.GoalDao
import kotlinx.coroutines.flow.Flow

class GoalLocalDataSourceImpl(
    private val goalDao: GoalDao
) : GoalLocalDataSource {
    override fun getAllGoals(): Flow<List<GoalEntity>> = goalDao.getAllGoals()
    override suspend fun getGoalById(id: String): GoalEntity? = goalDao.getGoalById(id)
    override suspend fun upsertGoal(goal: GoalEntity) = goalDao.upsertGoal(goal)
    override suspend fun deleteGoal(goal: GoalEntity) = goalDao.deleteGoal(goal)
    override suspend fun deleteGoalsByIds(ids: List<String>) = goalDao.deleteGoalsByIds(ids)
    override suspend fun deleteAllGoals() = goalDao.deleteAllGoals()
}
