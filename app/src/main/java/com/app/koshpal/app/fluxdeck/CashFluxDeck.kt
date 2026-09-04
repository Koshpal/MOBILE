package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.*
import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CashFluxDeck {

    private val _allTransactions = MutableStateFlow(Transactions(emptyList()))
    val allTransactions = _allTransactions.asStateFlow()

    private val _allBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val allBudgets = _allBudgets.asStateFlow()

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags = _allTags.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _typeFilter = MutableStateFlow("All")
    val typeFilter = _typeFilter.asStateFlow()

    private val _filterPeriod = MutableStateFlow("All")
    val filterPeriod = _filterPeriod.asStateFlow()

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Long?>(null)
    val endDate = _endDate.asStateFlow()

    private val _showExcluded = MutableStateFlow(false)
    val showExcluded = _showExcluded.asStateFlow()

    val cashBalance = _allTransactions.map { transactions ->
        transactions.transactions.filter { it.isCash && (!it.isExcludedFromCashFlow || _showExcluded.value) }
            .sumOf { if (it.type == TransactionType.EXPENSE) -it.amount else it.amount }
    }

    val filteredTransactions = combine(
        _allTransactions, _searchQuery, _typeFilter, _filterPeriod, _startDate, _endDate, _showExcluded
    ) { args ->
        val list = args[0] as Transactions
        val query = args[1] as String
        val type = args[2] as String
        val period = args[3] as String
        val start = args[4] as Long?
        val end = args[5] as Long?
        val showEx = args[6] as Boolean

        list.transactions.asSequence().filter { it.isCash }
            .filter { if (!showEx) !it.isExcludedFromCashFlow else true }
            .filter {
                val partyName = if (it.type == TransactionType.INCOME) it.senderName else it.receiverName
                partyName.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            .filter {
                when (type) {
                    "Income" -> it.type == TransactionType.INCOME
                    "Expense" -> it.type == TransactionType.EXPENSE
                    else -> true
                }
            }
            .filter {
                if (start != null && end != null) {
                    it.transactionDate in start..end
                } else {
                    val now = LocalDate.now()
                    val txnDate = Instant.ofEpochMilli(it.transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
                    when (period) {
                        "This Month" -> txnDate.month == now.month && txnDate.year == now.year
                        "This Year" -> txnDate.year == now.year
                        else -> true
                    }
                }
            }.toList()
    }

    val cashTrend = _allTransactions.map { list ->
        val cashOnly = list.transactions.filter { it.isCash && (!it.isExcludedFromCashFlow || _showExcluded.value) }
            .sortedBy { it.transactionDate }
        
        var balance = 0.0
        cashOnly.map { 
            balance += if (it.type == TransactionType.EXPENSE) -it.amount else it.amount
            balance
        }
    }

    val trendDateRange = _allTransactions.map { list ->
        val cashOnly = list.transactions.filter { it.isCash }.sortedBy { it.transactionDate }
        if (cashOnly.isEmpty()) return@map "No data" to "No data"
        
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
        val start = Instant.ofEpochMilli(cashOnly.first().transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val end = Instant.ofEpochMilli(cashOnly.last().transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
        
        start.format(formatter) to end.format(formatter)
    }

    val availableBounds = _allTransactions.map { list ->
        if (list.transactions.isEmpty()) {
            val now = System.currentTimeMillis()
            Pair(now, now)
        } else {
            Pair(list.transactions.minOf { it.transactionDate }, list.transactions.maxOf { it.transactionDate })
        }
    }

    fun updateAllTransactions(list: Transactions) { _allTransactions.value = list }
    fun updateAllBudgets(list: List<Budget>) { _allBudgets.value = list }
    fun updateAllTags(list: List<Tag>) { _allTags.value = list }
    
    fun getBudgetName(id: String?): String? = allBudgets.value.find { it.id == id }?.title
    fun getTagName(id: String?): String? = allTags.value.find { it.id == id }?.name
    fun getCategoryName(budgetId: String?, categoryId: String?): String? {
        val budget = allBudgets.value.find { it.id == budgetId }
        return budget?.allocations?.find { it.categoryId == categoryId }?.category?.title
    }

    fun clear() {
        _allTransactions.value = Transactions(emptyList())
        _allBudgets.value = emptyList()
        _allTags.value = emptyList()
        _searchQuery.value = ""
        _typeFilter.value = "All"
        _filterPeriod.value = "All"
        _startDate.value = null
        _endDate.value = null
        _showExcluded.value = false
    }

    fun updateSearchQuery(value: String) { _searchQuery.value = value }
    fun updateTypeFilter(value: String) { _typeFilter.value = value }
    fun updateFilterPeriod(value: String) { _filterPeriod.value = value }
    fun updateDateRange(start: Long, end: Long) {
        _startDate.value = start
        _endDate.value = end
    }
    fun toggleShowExcluded() { _showExcluded.value = !_showExcluded.value }
}
