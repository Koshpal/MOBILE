package com.app.koshpal.app.domain.model

import java.util.UUID

data class Tag(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val budgetGoal: Double,
    val colorHex: String
)
