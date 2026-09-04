package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.mapper.toNotification
import com.app.koshpal.app.data.mapper.toNotificationEntity
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.repository.NotificationRepo
import com.app.koshpal.core.data.local.source.NotificationLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepoImpl(
    private val localDataSource: NotificationLocalDataSource
) : NotificationRepo {
    override suspend fun insertNotification(notification: Notification) {
        localDataSource.insertNotification(notification.toNotificationEntity())
    }

    override fun getAllNotifications(): Flow<List<Notification>> {
        return localDataSource.getAllNotifications().map { entities ->
            entities.map { it.toNotification() }
        }
    }

    override fun getNotificationsInRange(startTime: Long, endTime: Long): Flow<List<Notification>> {
        return localDataSource.getNotificationsInRange(startTime, endTime).map { entities ->
            entities.map { it.toNotification() }
        }
    }

    override suspend fun deleteOldNotifications(threshold: Long) {
        localDataSource.deleteOldNotifications(threshold)
    }

    override suspend fun markAsRead(id: String) {
        localDataSource.markAsRead(id)
    }

    override suspend fun clearAll() {
        localDataSource.clearAll()
    }
}
