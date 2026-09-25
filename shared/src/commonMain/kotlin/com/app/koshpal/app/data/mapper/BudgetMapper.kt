package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.BudgetAllocation
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.core.data.entities.BudgetEntity
import com.app.koshpal.core.data.entities.BudgetWithDetails
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.data.remote.dto.BudgetDto
import com.app.koshpal.core.data.remote.dto.CategoryWithAmountDto
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
        lastModifiedTimeStamp = Clock.System.now().toEpochMilliseconds(),
        isSynced = this.isSynced,
    )
}

@OptIn(ExperimentalUuidApi::class)
fun BudgetDto.toLocalBudget(): Budget {
    val budgetId = this.id ?: Uuid.random().toString()
    return Budget(
        id = budgetId,
        title = (this.title ?: "").take(20),
        amount = this.amount ?: 0.0,
        period = this.period ?: BudgetPeriod.UNKNOWN,
        startDate = this.startDate ?: "",
        endDate = this.endDate,
        budgetType = this.budgetType ?: BudgetType.UNKNOWN,
        isSynced = true,
        allocations = (this.categories ?: emptyList()).map { cat ->
            val catId = cat.id ?: Uuid.random().toString()
            BudgetAllocation(
                id = Uuid.random().toString(),
                budgetId = budgetId,
                categoryId = catId,
                allocatedAmount = cat.allottedAmount ?: 0.0,
                category = Category(
                    id = catId,
                    title = (cat.name ?: "").take(20),
                    iconResId = cat.iconResId,
                    colorHex = cat.colorHex ?: "0xFF9E9E9E",
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
            CategoryWithAmountDto(
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
