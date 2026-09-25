package com.app.koshpal.app.fluxdeck

import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Instant

private val MONTH_ABBRS = arrayOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

data class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {
    fun minusMonths(months: Long): YearMonth {
        var newMonth = month - (months % 12).toInt()
        var newYear = year - (months / 12).toInt()
        if (newMonth < 1) {
            newMonth += 12
            newYear -= 1
        }
        return YearMonth(newYear, newMonth)
    }

    fun plusMonths(months: Long): YearMonth {
        var newMonth = month + (months % 12).toInt()
        var newYear = year + (months / 12).toInt()
        if (newMonth > 12) {
            newMonth -= 12
            newYear += 1
        }
        return YearMonth(newYear, newMonth)
    }

    override fun compareTo(other: YearMonth): Int {
        val y = year.compareTo(other.year)
        if (y != 0) return y
        return month.compareTo(other.month)
    }

    companion object {
        fun now(): YearMonth {
            val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return YearMonth(date.year, date.month.number)
        }

        fun from(date: LocalDate): YearMonth = YearMonth(date.year, date.month.number)
    }
}

class CashFlowFluxDeck(
    transactionsFluxDeck: TransactionsFluxDeck,
    goalFluxDeck: GoalFluxDeck,
) {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedMonth = MutableStateFlow<YearMonth?>(YearMonth.now())
    val selectedMonth = _selectedMonth.asStateFlow()

    val allTransactions = transactionsFluxDeck.allTransactions

    val activeTransactions = combine(allTransactions, _selectedMonth) { list, yearMonth ->
        list.transactions.filter { txn ->
            if (txn.isExcludedFromCashFlow) return@filter false
            if (yearMonth == null) return@filter true
            val date = Instant.fromEpochMilliseconds(txn.transactionDate)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
            YearMonth.from(date) == yearMonth
        }
    }

    val incomeThisMonth = activeTransactions.map { list ->
        list.filter { it.type == TransactionType.INCOME }.sumOf { abs(it.amount) }
    }

    val expenseThisMonth = activeTransactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) }
    }

    val leftThisMonth = combine(incomeThisMonth, expenseThisMonth) { inc, exp ->
        (inc - exp).coerceAtLeast(0.0)
    }

    val investedThisMonth = goalFluxDeck.allGoals.map { goals ->
        goals.sumOf { it.savedAmount }
    }

    val incomingTransactions = combine(activeTransactions, _searchQuery) { list, query ->
        list.filter { it.type == TransactionType.INCOME }
            .filter { txn ->
                query.isBlank() ||
                        (txn.description?.contains(query, ignoreCase = true) == true) ||
                        txn.senderName.contains(query, ignoreCase = true) ||
                        txn.contactName?.contains(query, ignoreCase = true) == true ||
                        txn.bank.contains(query, ignoreCase = true)
            }
    }

    val outgoingTransactions = combine(activeTransactions, _searchQuery) { list, query ->
        list.filter { it.type == TransactionType.EXPENSE }
            .filter { txn ->
                query.isBlank() ||
                        (txn.description?.contains(query, ignoreCase = true) == true) ||
                        txn.receiverName.contains(query, ignoreCase = true) ||
                        txn.contactName?.contains(query, ignoreCase = true) == true ||
                        txn.bank.contains(query, ignoreCase = true)
            }
    }

    val dualLineTrendData: Flow<List<CashFlowPoint>> = combine(allTransactions, _selectedMonth) { list, targetMonth ->
        val anchorMonth = targetMonth ?: YearMonth.now()
        val months = (5 downTo 0).map { anchorMonth.minusMonths(it.toLong()) }

        months.map { ym ->
            val monthTxns = list.transactions.filter {
                if (it.isExcludedFromCashFlow) return@filter false
                val date = Instant.fromEpochMilliseconds(it.transactionDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                YearMonth.from(date) == ym
            }
            val inc = monthTxns.filter { it.type == TransactionType.INCOME }.sumOf { abs(it.amount) }
            val exp = monthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) }
            val monthLabel = MONTH_ABBRS[ym.month - 1]

            CashFlowPoint(
                monthLabel = monthLabel,
                yearMonth = ym,
                incoming = inc,
                outgoing = exp,
            )
        }
    }

    data class CashFlowPoint(
        val monthLabel: String,
        val yearMonth: YearMonth,
        val incoming: Double,
        val outgoing: Double,
    )

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateSelectedMonth(ym: YearMonth?) { _selectedMonth.value = ym }

    fun selectPreviousMonth() {
        val current = _selectedMonth.value ?: YearMonth.now()
        _selectedMonth.value = current.minusMonths(1)
    }

    fun selectNextMonth() {
        val current = _selectedMonth.value ?: YearMonth.now()
        _selectedMonth.value = current.plusMonths(1)
    }

    fun toggleAllTime(showAll: Boolean) {
        _selectedMonth.value = if (showAll) null else YearMonth.now()
    }

    fun clear() {
        _searchQuery.value = ""
        _selectedMonth.value = YearMonth.now()
    }
}
