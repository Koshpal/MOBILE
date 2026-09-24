package com.app.koshpal.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.koshpal.core.data.entities.DueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDue(due: DueEntity)

    @Update
    suspend fun updateDue(due: DueEntity)

    @Delete
    suspend fun deleteDue(due: DueEntity)

    @Query("DELETE FROM dues WHERE id IN (:ids)")
    suspend fun deleteDuesByIds(ids: List<String>)

    @Query("SELECT * FROM dues ORDER BY date DESC")
    fun getAllDues(): Flow<List<DueEntity>>

    @Query("SELECT * FROM dues WHERE id = :id LIMIT 1")
    suspend fun getDueById(id: String): DueEntity?
}
