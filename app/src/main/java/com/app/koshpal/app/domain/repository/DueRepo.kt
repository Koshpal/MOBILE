package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface DueRepo {
    fun getAllDues(): Flow<List<Due>>
    suspend fun getDueById(id: String): Result<Due?, DatabaseCallError>
    suspend fun insertDue(due: Due): Result<Unit, DatabaseCallError>
    suspend fun updateDue(due: Due): Result<Unit, DatabaseCallError>
    suspend fun deleteDue(due: Due): Result<Unit, DatabaseCallError>
    suspend fun deleteDuesByIds(ids: List<String>): Result<Unit, DatabaseCallError>
}
