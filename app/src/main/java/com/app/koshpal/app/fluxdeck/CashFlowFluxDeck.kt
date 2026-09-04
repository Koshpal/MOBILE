package com.app.koshpal.app.fluxdeck

import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

class CashFlowFluxDeck(
    private val transactionsFluxDeck: TransactionsFluxDeck,
    private val goalFluxDeck: GoalFluxDeck,
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
            val date = Instant.ofEpochMilli(txn.transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
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
                        txn.description.contains(query, ignoreCase = true) ||
                        txn.senderName.contains(query, ignoreCase = true) ||
                        txn.contactName?.contains(query, ignoreCase = true) == true ||
                        txn.bank.contains(query, ignoreCase = true)
            }
    }

    val outgoingTransactions = combine(activeTransactions, _searchQuery) { list, query ->
        list.filter { it.type == TransactionType.EXPENSE }
            .filter { txn ->
                query.isBlank() ||
                        txn.description.contains(query, ignoreCase = true) ||
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
                val date = Instant.ofEpochMilli(it.transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
                YearMonth.from(date) == ym
            }
            val inc = monthTxns.filter { it.type == TransactionType.INCOME }.sumOf { abs(it.amount) }
            val exp = monthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) }
            val monthLabel = ym.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH))

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
