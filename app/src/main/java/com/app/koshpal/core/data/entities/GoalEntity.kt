package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val monthlySavings: Double,
    val durationMonths: Int,
    val iconResId: String,
    val colorHex: String,
    val creationDate: String,
    val isAchieved: Boolean,
    val tagId: String?,
    val imageUri: String?,
    val lastModifiedTimeStamp: Long,
    val isSynced: Boolean = false
)
