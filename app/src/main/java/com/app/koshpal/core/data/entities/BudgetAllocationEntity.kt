package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "budget_allocations",
    foreignKeys = [
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["id"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("budgetId"), Index("categoryId")]
)

data class BudgetAllocationEntity(
    @PrimaryKey 
    val id: String = UUID.randomUUID().toString(),
    val budgetId: String,
    val categoryId: String,
    val allocatedAmount: Double
)