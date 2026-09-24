package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.mapper.toReminderType
import com.app.koshpal.app.data.mapper.toReminderTypeEntity
import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.app.domain.model.defaultReminderTypes
import com.app.koshpal.app.domain.repository.ReminderTypeRepo
import com.app.koshpal.core.data.local.source.ReminderTypeLocalDataSource
import com.app.koshpal.core.data.networking.safeDatabaseCall
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.*

class ReminderTypeRepoImpl(
    private val localDataSource: ReminderTypeLocalDataSource
) : ReminderTypeRepo {
    override fun getAllReminderTypes(): Flow<List<ReminderType>> {
        return localDataSource.getAllReminderTypes().map { entities ->
            val fromDb = entities.map { it.toReminderType() }
            val combined = (defaultReminderTypes + fromDb).distinctBy { it.name.lowercase() }
            combined
        }.onStart { emit(defaultReminderTypes) }
    }

    override suspend fun getReminderTypeById(id: String): Result<ReminderType?, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.getReminderTypeById(id)?.toReminderType()
        }
    }

    override suspend fun insertReminderType(type: ReminderType): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.insertReminderType(type.toReminderTypeEntity())
        }
    }

    override suspend fun updateReminderType(type: ReminderType): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.updateReminderType(type.toReminderTypeEntity())
        }
    }

    override suspend fun deleteReminderType(type: ReminderType): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteReminderType(type.toReminderTypeEntity())
        }
    }
}
