package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.BudgetAllocation
import com.app.koshpal.core.data.entities.AllocationWithCategory

fun AllocationWithCategory.toBudgetAllocation(): BudgetAllocation {
    return BudgetAllocation(
        id = this.allocation.id,
        budgetId = this.allocation.budgetId,
        categoryId = this.allocation.categoryId,
        allocatedAmount = this.allocation.allocatedAmount,
        category = this.category.toCategory()
    )
}