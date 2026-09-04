package com.app.koshpal.app.domain.model

import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val monthlySavings: Double,
    val durationMonths: Int,
    val iconResId: String,
    val colorHex: String,
    val creationDate: String,
    val isAchieved: Boolean = false,
    val tagId: String? = null,
    val imageUri: String? = null,
    val isSynced: Boolean = false
) {
    val progress: Float
        get() = if (targetAmount > 0) (savedAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    
    val progressPercentage: Int
        get() = (progress * 100).toInt()
}
