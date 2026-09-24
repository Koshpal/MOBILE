package com.app.koshpal.app.domain.model

data class GoalSavingsSummary(
        val totalTimesSaving: Int,
        val monthlyDepositAmount: Double,
        val monthlyDepositTimes: Int,
        val monthlyDepositPercentage: Int,
        val manualTopUpAmount: Double,
        val manualTopUpTimes: Int,
        val manualTopUpPercentage: Int
    )
    