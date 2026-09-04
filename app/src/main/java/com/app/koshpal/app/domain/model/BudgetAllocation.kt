package com.app.koshpal.app.domain.model

data class BudgetAllocation(
    val id: String,
    val budgetId: String,
    val categoryId: String,
    val allocatedAmount: Double = 0.0,
    val category: Category? = null
)