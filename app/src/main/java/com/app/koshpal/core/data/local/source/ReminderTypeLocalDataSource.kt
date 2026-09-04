package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.ReminderTypeEntity
import kotlinx.coroutines.flow.Flow

interface ReminderTypeLocalDataSource {
    fun getAllReminderTypes(): Flow<List<ReminderTypeEntity>>
    suspend fun getReminderTypeById(id: String): ReminderTypeEntity?
    suspend fun insertReminderType(type: ReminderTypeEntity)
    suspend fun updateReminderType(type: ReminderTypeEntity)
    suspend fun deleteReminderType(type: ReminderTypeEntity)
}
