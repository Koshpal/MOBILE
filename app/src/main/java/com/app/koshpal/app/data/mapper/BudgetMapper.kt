package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.BudgetAllocation
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.core.data.entities.BudgetEntity
import com.app.koshpal.core.data.entities.BudgetWithDetails
import com.app.koshpal.core.data.remote.dto.BudgetDto
import java.util.UUID

fun BudgetWithDetails.toBudget(): Budget {
    val mappedAllocations = this.allocations.map { it.toBudgetAllocation() }

    return Budget(
        id = this.budget.id,
        title = this.budget.title.take(20),
        amount = this.budget.amount,
        period = this.budget.period,
        startDate = this.budget.startDate,
        endDate = this.budget.endDate,
        budgetType = this.budget.budgetType,
        allocations = mappedAllocations,
        isSynced = this.budget.isSynced,
    )
}

fun BudgetEntity.toBudget(): Budget {
    return Budget(
        id = this.id,
        title = this.title.take(20),
        amount = this.amount,
        period = this.period,
        startDate = this.startDate,
        endDate = this.endDate,
        budgetType = this.budgetType,
        allocations = emptyList(),
        isSynced = this.isSynced,
    )
}

fun Budget.toBudgetEntity(): BudgetEntity {
    return BudgetEntity(
        id = this.id,
        title = this.title.take(20),
        amount = this.amount,
        period = this.period,
        startDate = this.startDate,
        endDate = this.endDate,
        budgetType = this.budgetType,
        lastModifiedTimeStamp = System.currentTimeMillis(),
        isSynced = this.isSynced,
    )
}

fun BudgetDto.toLocalBudget(): Budget {
    return Budget(
        id = this.id,
        title = this.title.take(20),
        amount = this.amount,
        period = this.period,
        startDate = this.startDate,
        endDate = this.endDate,
        budgetType = this.budgetType,
        isSynced = true,
        allocations = this.categories.map { cat ->
            BudgetAllocation(
                id = UUID.randomUUID().toString(),
                budgetId = this.id,
                categoryId = cat.id,
                allocatedAmount = cat.allottedAmount,
                category = Category(
                    id = cat.id,
                    title = cat.name.take(20),
                    iconResId = cat.iconResId,
                    colorHex = cat.colorHex,
                    parentCategoryId = cat.parentCategoryId,
                ),
            )
        },
    )
}

fun Budget.toBudgetDto(): BudgetDto {
    val cleanEndDate = if (this.endDate == "Select a date" || this.endDate.isNullOrBlank()) null else this.endDate
    return BudgetDto(
        id = this.id,
        title = this.title.take(20),
        amount = this.amount,
        period = this.period,
        startDate = this.startDate,
        endDate = cleanEndDate,
        budgetType = this.budgetType,
        categories = this.allocations.map { alloc ->
            com.app.koshpal.core.data.remote.dto.CategoryWithAmountDto(
                id = alloc.categoryId,
                name = alloc.category?.title?.take(20) ?: "Unknown",
                iconResId = alloc.category?.iconResId ?: "category",
                colorHex = alloc.category?.colorHex ?: "0xFF9E9E9E",
                parentCategoryId = alloc.category?.parentCategoryId,
                allottedAmount = alloc.allocatedAmount,
            )
        },
    )
}
