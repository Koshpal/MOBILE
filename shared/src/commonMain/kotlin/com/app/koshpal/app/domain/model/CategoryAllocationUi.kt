package com.app.koshpal.app.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class CategoryAllocationUi(
    val category: Category,
    val amountString: String = ""
) {
    val amountDouble: Double get() = amountString.toDoubleOrNull() ?: 0.0

    fun toEntity(budgetId: String): BudgetAllocation {
        return BudgetAllocation(
            id = Uuid.random().toString(),
            budgetId = budgetId,
            categoryId = category.id,
            allocatedAmount = amountDouble,
            category = category
        )
    }
}

typealias CategoryAllocationUiState = CategoryAllocationUi