package com.app.koshpal.app.domain.model

import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import java.time.ZoneId
import java.util.UUID

data class Budget(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: String,
    val endDate: String? = null,
    val budgetType: BudgetType,
    val allocations: List<BudgetAllocation>,
    val isSynced: Boolean = false,
) {
    val categories: List<Category> get() = allocations.mapNotNull { it.category }

    fun getDateRange(): Pair<Long, Long>? {
        return try {
            val start = startDate.parseIsoToLocalDate() ?: return null
            val end = if (budgetType == BudgetType.ONE_TIME) {
                if (!endDate.isNullOrBlank() && endDate != "Select a date") {
                    endDate.parseIsoToLocalDate() ?: start
                } else {
                    start
                }
            } else {
                when (period) {
                    BudgetPeriod.Weekly -> start.plusWeeks(1)
                    BudgetPeriod.Monthly -> start.plusMonths(1)
                    BudgetPeriod.Yearly -> start.plusYears(1)
                }
            }

            val startMillis = start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            startMillis to endMillis
        } catch (_: Exception) {
            null
        }
    }
}
