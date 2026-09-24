package com.app.koshpal.app.domain.model

data class TagSummary(
    val tag: Tag,
    val transactionCount: Int,
    val goalCount: Int,
    val totalSpent: Double,
    val totalIncoming: Double,
    val associatedCategories: List<Category>,
    val associatedGoals: List<Goal> = emptyList(),
    val insightText: String = ""
)
