package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.core.data.entities.NotificationEntity

fun NotificationEntity.toNotification(): Notification {
    return Notification(
        id = id,
        type = type,
        title = title,
        message = message,
        timestamp = timestamp,
        featureId = featureId,
        iconResId = iconResId,
        isRead = isRead
    )
}

fun Notification.toNotificationEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        type = type,
        title = title,
        message = message,
        timestamp = timestamp,
        featureId = featureId,
        iconResId = iconResId,
        isRead = isRead
    )
}
