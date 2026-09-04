package com.app.koshpal.core.data.remote.dto

import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import kotlinx.serialization.Serializable

@Serializable
data class BudgetDto(
    val id: String,
    val title: String,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: String,
    val endDate: String? = null,
    val budgetType: BudgetType,
    val categories: List<CategoryWithAmountDto>
)