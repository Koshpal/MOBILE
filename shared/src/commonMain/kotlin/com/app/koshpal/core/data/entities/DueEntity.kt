package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dues")
data class DueEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val date: String,
    val amount: Double,
    val status: String,
    val frequency: String,
    val type: String,
    val reminderType: String?,
    val overdueInfo: String? = null,
    val isCompleted: Boolean,
    val iconResId: String? = null,
    val colorHex: String? = null,
    val reminderTime: Long? = null,
    val customFrequencyDays: Int? = null
)
