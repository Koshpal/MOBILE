package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.koshpal.core.data.entities.enums.NotificationType
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String = Uuid.random().toString(),
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val featureId: String? = null,
    val iconResId: String? = null,
    val isRead: Boolean = false
)
