package com.app.koshpal.app.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Tag(
    val id: String = Uuid.random().toString(),
    val name: String,
    val budgetGoal: Double,
    val colorHex: String
)
