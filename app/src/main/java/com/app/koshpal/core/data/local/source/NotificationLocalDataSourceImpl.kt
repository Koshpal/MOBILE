package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.NotificationEntity
import com.app.koshpal.core.data.local.dao.NotificationDao
import kotlinx.coroutines.flow.Flow

class NotificationLocalDataSourceImpl(
    private val notificationDao: NotificationDao
) : NotificationLocalDataSource {
    override suspend fun insertNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    override fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    override fun getNotificationsInRange(startTime: Long, endTime: Long): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsInRange(startTime, endTime)
    }

    override suspend fun deleteOldNotifications(threshold: Long) {
        notificationDao.deleteOldNotifications(threshold)
    }

    override suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    override suspend fun clearAll() {
        notificationDao.clearAll()
    }
}
