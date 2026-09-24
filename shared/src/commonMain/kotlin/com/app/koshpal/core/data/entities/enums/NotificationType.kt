package com.app.koshpal.core.data.entities.enums

import kotlinx.serialization.Serializable

@Serializable
enum class NotificationType {
    GOAL_INSIGHT,
    TRANSACTION_ALERT,
    DUE_REMINDER,
    BUDGET_WATCH,
    ANOMALY_DETECTION
}
