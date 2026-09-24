package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.DueEntity
import com.app.koshpal.core.data.local.dao.DueDao
import kotlinx.coroutines.flow.Flow

class DueLocalDataSourceImpl(
    private val dueDao: DueDao
) : DueLocalDataSource {
    override fun getAllDues(): Flow<List<DueEntity>> = dueDao.getAllDues()
    override suspend fun getDueById(id: String): DueEntity? = dueDao.getDueById(id)
    override suspend fun insertDue(due: DueEntity) = dueDao.insertDue(due)
    override suspend fun updateDue(due: DueEntity) = dueDao.updateDue(due)
    override suspend fun deleteDue(due: DueEntity) = dueDao.deleteDue(due)
    override suspend fun deleteDuesByIds(ids: List<String>) = dueDao.deleteDuesByIds(ids)
}
