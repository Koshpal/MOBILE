package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ReminderTypeRepo {
    fun getAllReminderTypes(): Flow<List<ReminderType>>
    suspend fun getReminderTypeById(id: String): Result<ReminderType?, DatabaseCallError>
    suspend fun insertReminderType(type: ReminderType): Result<Unit, DatabaseCallError>
    suspend fun updateReminderType(type: ReminderType): Result<Unit, DatabaseCallError>
    suspend fun deleteReminderType(type: ReminderType): Result<Unit, DatabaseCallError>
}
