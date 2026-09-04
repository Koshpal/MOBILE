package com.app.koshpal.app.domain.model

import com.app.koshpal.app.domain.model.BudgetAllocation
import com.app.koshpal.app.domain.model.Category
import java.util.UUID

data class CategoryAllocationUiState(
    val category: Category,
    val amountString: String = ""
) {
    val amountDouble: Double get() = amountString.toDoubleOrNull() ?: 0.0

    fun toEntity(budgetId: String): BudgetAllocation {
        return BudgetAllocation(
            id = UUID.randomUUID().toString(),
            budgetId = budgetId,
            categoryId = category.id,
            allocatedAmount = amountDouble,
            category = category
        )
    }
}
