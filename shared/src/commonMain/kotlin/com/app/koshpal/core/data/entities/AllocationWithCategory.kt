package com.app.koshpal.core.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AllocationWithCategory(
    @Embedded val allocation: BudgetAllocationEntity,
    @Relation(
        entity = CategoryEntity::class,
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

data class BudgetWithDetails(
    @Embedded val budget: BudgetEntity,
    @Relation(
        entity = BudgetAllocationEntity::class,
        parentColumn = "id",
        entityColumn = "budgetId"
    )
    val allocations: List<AllocationWithCategory>
)