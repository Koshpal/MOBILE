package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationLocalDataSource {
    suspend fun insertNotification(notification: NotificationEntity)
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    fun getNotificationsInRange(startTime: Long, endTime: Long): Flow<List<NotificationEntity>>
    suspend fun deleteOldNotifications(threshold: Long)
    suspend fun markAsRead(id: String)
    suspend fun clearAll()
}
