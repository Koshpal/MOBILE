package com.app.koshpal.app.domain.model

import com.app.koshpal.core.data.entities.enums.NotificationType

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long,
    val featureId: String? = null,
    val iconResId: String? = null,
    val isRead: Boolean = false
)
