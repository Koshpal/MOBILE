package com.app.koshpal.core.data.local.dao

import androidx.room.*
import com.app.koshpal.core.data.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getNotificationsInRange(startTime: Long, endTime: Long): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications WHERE timestamp < :threshold")
    suspend fun deleteOldNotifications(threshold: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
