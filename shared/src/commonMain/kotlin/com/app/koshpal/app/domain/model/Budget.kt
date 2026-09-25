package com.app.koshpal.app.domain.model

import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Budget(
    val id: String = Uuid.random().toString(),
    val title: String,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: String,
    val endDate: String? = null,
    val budgetType: BudgetType,
    val isRepeating: Boolean = false,
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
                    BudgetPeriod.Weekly -> start.plus(7, DateTimeUnit.DAY)
                    BudgetPeriod.Monthly -> start.plus(1, DateTimeUnit.MONTH)
                    BudgetPeriod.Yearly -> start.plus(1, DateTimeUnit.YEAR)
                    else -> start.plus(1, DateTimeUnit.MONTH)
                }
            }

            val startMillis = Instant.parse("${start}T00:00:00Z").toEpochMilliseconds()
            val endMillis = Instant.parse("${end}T23:59:59Z").toEpochMilliseconds()
            startMillis to endMillis
        } catch (_: Exception) {
            null
        }
    }
}
