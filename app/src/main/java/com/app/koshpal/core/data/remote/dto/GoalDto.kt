package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeGoalsResponseDto(
    val financialGoals: List<GoalDto> = emptyList(),
)

@Serializable
data class GoalDto(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val monthlySavings: Double = 0.0,
    val durationMonths: Int? = null,
    val iconResId: String? = null,
    val colorHex: String? = null,
    val creationDate: String? = null,
    val isAchieved: Boolean = false,
    val tagId: String? = null,
    val imageUri: String? = null,
    val goalDate: String? = null,
)
