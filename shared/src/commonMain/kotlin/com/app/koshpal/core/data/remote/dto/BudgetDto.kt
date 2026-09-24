package com.app.koshpal.core.data.remote.dto

import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import kotlinx.serialization.Serializable

@Serializable
data class BudgetDto(
    val id: String? = null,
    val title: String? = "",
    val amount: Double? = 0.0,
    val period: BudgetPeriod? = BudgetPeriod.UNKNOWN,
    val startDate: String? = "",
    val endDate: String? = null,
    val budgetType: BudgetType? = BudgetType.UNKNOWN,
    val categories: List<CategoryWithAmountDto>? = emptyList()
)
