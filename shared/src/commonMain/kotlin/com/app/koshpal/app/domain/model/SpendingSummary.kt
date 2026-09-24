package com.app.koshpal.app.domain.model

data class SpendingSummary(
    val outgoing: Double = 0.0,
    val incoming: Double = 0.0,
    val budgetUsed: Double = 0.0
)
