package com.app.koshpal.app.domain.model

data class TagDetailAnalytics(
    val tag: Tag,
    val totalSpent: Double,
    val transactionCount: Int,
    val remainingToSave: Double,
    val recommendedPerDay: Double,
    val progress: Float,
    val categories: List<TagCategoryAnalytics>,
    val totalAllotted: Double,
    val filteredTransactions: Transactions = Transactions(emptyList()),
    val goals: List<Goal> = emptyList()
)

data class TagCategoryAnalytics(
    val category: Category,
    val spent: Double,
    val allotted: Double,
    val percentage: Int
)
