package com.app.koshpal.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.koshpal.core.data.entities.ReminderTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderType(type: ReminderTypeEntity)

    @Update
    suspend fun updateReminderType(type: ReminderTypeEntity)

    @Delete
    suspend fun deleteReminderType(type: ReminderTypeEntity)

    @Query("SELECT * FROM reminder_types ORDER BY name ASC")
    fun getAllReminderTypes(): Flow<List<ReminderTypeEntity>>

    @Query("SELECT * FROM reminder_types WHERE id = :id LIMIT 1")
    suspend fun getReminderTypeById(id: String): ReminderTypeEntity?
}
