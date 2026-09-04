package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.koshpal.core.data.entities.enums.NotificationType
import java.util.UUID

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val featureId: String? = null,
    val iconResId: String? = null,
    val isRead: Boolean = false
)
