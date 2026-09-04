package com.app.koshpal.app.domain.model

data class Due(
    val id: String,
    val title: String,
    val date: String,
    val amount: Double,
    val status: String,
    val frequency: String,
    val type: String,
    val reminderType: String?,
    val overdueInfo: String? = null,
    val isCompleted: Boolean = false,
    val iconResId: String? = null,
    val colorHex: String? = null,
    val reminderTime: Long? = null,
    val customFrequencyDays: Int? = null
)
