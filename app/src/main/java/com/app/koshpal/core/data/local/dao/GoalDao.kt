package com.app.koshpal.core.data.local.dao

import androidx.room.*
import com.app.koshpal.core.data.entities.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY lastModifiedTimeStamp DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: String): GoalEntity?

    @Upsert
    suspend fun upsertGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id IN (:ids)")
    suspend fun deleteGoalsByIds(ids: List<String>)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()
}
