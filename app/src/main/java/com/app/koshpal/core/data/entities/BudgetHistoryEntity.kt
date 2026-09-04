package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_history")
data class BudgetHistoryEntity(
    @PrimaryKey val budgetId: String,
    val archivedTimestamp: Long
)