package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.ReminderTypeEntity
import com.app.koshpal.core.data.local.dao.ReminderTypeDao
import kotlinx.coroutines.flow.Flow

class ReminderTypeLocalDataSourceImpl(
    private val reminderTypeDao: ReminderTypeDao
) : ReminderTypeLocalDataSource {
    override fun getAllReminderTypes(): Flow<List<ReminderTypeEntity>> {
        return reminderTypeDao.getAllReminderTypes()
    }

    override suspend fun getReminderTypeById(id: String): ReminderTypeEntity? {
        return reminderTypeDao.getReminderTypeById(id)
    }

    override suspend fun insertReminderType(type: ReminderTypeEntity) {
        reminderTypeDao.insertReminderType(type)
    }

    override suspend fun updateReminderType(type: ReminderTypeEntity) {
        reminderTypeDao.updateReminderType(type)
    }

    override suspend fun deleteReminderType(type: ReminderTypeEntity) {
        reminderTypeDao.deleteReminderType(type)
    }
}
