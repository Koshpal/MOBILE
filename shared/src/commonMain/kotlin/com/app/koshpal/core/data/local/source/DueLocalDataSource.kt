package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.DueEntity
import kotlinx.coroutines.flow.Flow

interface DueLocalDataSource {
    fun getAllDues(): Flow<List<DueEntity>>
    suspend fun getDueById(id: String): DueEntity?
    suspend fun insertDue(due: DueEntity)
    suspend fun updateDue(due: DueEntity)
    suspend fun deleteDue(due: DueEntity)
    suspend fun deleteDuesByIds(ids: List<String>)
}
