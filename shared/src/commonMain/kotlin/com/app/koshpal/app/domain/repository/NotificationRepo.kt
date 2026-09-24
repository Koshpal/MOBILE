package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {
    suspend fun insertNotification(notification: Notification)
    fun getAllNotifications(): Flow<List<Notification>>
    fun getNotificationsInRange(startTime: Long, endTime: Long): Flow<List<Notification>>
    suspend fun deleteOldNotifications(threshold: Long)
    suspend fun markAsRead(id: String)
    suspend fun clearAll()
}
