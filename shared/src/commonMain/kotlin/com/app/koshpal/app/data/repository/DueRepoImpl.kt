package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.mapper.toDue
import com.app.koshpal.app.data.mapper.toDueEntity
import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.app.domain.repository.DueRepo
import com.app.koshpal.core.data.local.source.DueLocalDataSource
import com.app.koshpal.core.data.networking.safeDatabaseCall
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DueRepoImpl(
    private val localDataSource: DueLocalDataSource
) : DueRepo {
    override fun getAllDues(): Flow<List<Due>> {
        return localDataSource.getAllDues().map { entities ->
            entities.map { it.toDue() }
        }
    }

    override suspend fun getDueById(id: String): Result<Due?, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.getDueById(id)?.toDue()
        }
    }

    override suspend fun insertDue(due: Due): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.insertDue(due.toDueEntity())
        }
    }

    override suspend fun updateDue(due: Due): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.updateDue(due.toDueEntity())
        }
    }

    override suspend fun deleteDue(due: Due): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteDue(due.toDueEntity())
        }
    }

    override suspend fun deleteDuesByIds(ids: List<String>): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteDuesByIds(ids)
        }
    }
}
