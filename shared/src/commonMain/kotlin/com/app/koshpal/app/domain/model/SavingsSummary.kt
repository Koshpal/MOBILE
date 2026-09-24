package com.app.koshpal.app.domain.model

data class SavingsSummary(
    val totalSavingCount: Int,
    val monthlyDepositAmount: Double,
    val monthlyDepositCount: Int,
    val monthlyDepositPercentage: Float,
    val manualTopUpAmount: Double,
    val manualTopUpCount: Int,
    val manualTopUpPercentage: Float
)
