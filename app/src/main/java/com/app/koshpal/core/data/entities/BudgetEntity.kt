package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType

@Entity( tableName = "budgets",
 indices = [Index(value = ["startDate"]), Index(value = ["budgetType"])]
)
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: String,
    val endDate: String? = null,
    val budgetType: BudgetType,
    val lastModifiedTimeStamp: Long,
    val isSynced: Boolean = false
)
