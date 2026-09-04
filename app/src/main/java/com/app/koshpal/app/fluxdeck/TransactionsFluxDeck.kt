package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.states.SyncStatus
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

class TransactionsFluxDeck {
    private val _classifyIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val classifyIntent = _classifyIntent.asSharedFlow()

    private val _allTransactions = MutableStateFlow(Transactions(transactions = emptyList()))
    val allTransactions = _allTransactions.asStateFlow()

    private val _allBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val allBudgets = _allBudgets.asStateFlow()

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags = _allTags.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow("All")
    val selectedTab = _selectedTab.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus = _syncStatus.asStateFlow()

    private val _typeFilter = MutableStateFlow("Both")
    val typeFilter = _typeFilter.asStateFlow()

    private val _showBookmarked = MutableStateFlow(false)
    val showBookmarked = _showBookmarked.asStateFlow()

    private val _showCash = MutableStateFlow(false)
    val showCash = _showCash.asStateFlow()

    private val _showWithNotes = MutableStateFlow(false)
    val showWithNotes = _showWithNotes.asStateFlow()

    private val _showWithReceipts = MutableStateFlow(false)
    val showWithReceipts = _showWithReceipts.asStateFlow()

    private val _showWithoutPayorPayee = MutableStateFlow(false)
    val showWithoutPayorPayee = _showWithoutPayorPayee.asStateFlow()

    private val _showExcludedFromCashFlow = MutableStateFlow(false)
    val showExcludedFromCashFlow = _showExcludedFromCashFlow.asStateFlow()

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Long?>(null)
    val endDate = _endDate.asStateFlow()

    private val _transactionId = MutableStateFlow<String?>(null)
    val transactionId = _transactionId.asStateFlow()

    private val _selectedBudgetType = MutableStateFlow<BudgetType?>(null)
    val selectedBudgetType = _selectedBudgetType.asStateFlow()

    private val _selectedBudgetId = MutableStateFlow<String?>(null)
    val selectedBudgetId = _selectedBudgetId.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    private val _selectedParentCategoryId = MutableStateFlow<String?>(null)
    val selectedParentCategoryId = _selectedParentCategoryId.asStateFlow()

    private val _selectedTagIds = MutableStateFlow<List<String>>(emptyList())
    val selectedTagIds = _selectedTagIds.asStateFlow()

    private val _selectedTransactionType = MutableStateFlow<TransactionType?>(null)
    val selectedTransactionType = _selectedTransactionType.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes = _notes.asStateFlow()

    private val _senderName = MutableStateFlow("")
    val senderName = _senderName.asStateFlow()

    private val _receiverName = MutableStateFlow("")
    val receiverName = _receiverName.asStateFlow()

    private val _contactName = MutableStateFlow("")
    val contactName = _contactName.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount = _amount.asStateFlow()

    private val _bank = MutableStateFlow("")
    val bank = _bank.asStateFlow()

    private val _mode = MutableStateFlow("")
    val mode = _mode.asStateFlow()

    private val _date = MutableStateFlow(System.currentTimeMillis())
    val date = _date.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked = _isBookmarked.asStateFlow()

    private val _isCash = MutableStateFlow(false)
    val isCash = _isCash.asStateFlow()

    private val _hasReceipt = MutableStateFlow(false)
    val hasReceipt = _hasReceipt.asStateFlow()

    private val _isExcludedFromCashFlow = MutableStateFlow(false)
    val isExcludedFromCashFlow = _isExcludedFromCashFlow.asStateFlow()

    private val _isFromNotification = MutableStateFlow(false)
    val isFromNotification = _isFromNotification.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedPartyDate = MutableStateFlow<Long?>(null)
    val selectedPartyDate = _selectedPartyDate.asStateFlow()

    val filteredTransactions = combine(
        _allTransactions, _searchQuery, _selectedTab,
        _typeFilter, _showWithNotes, _showWithoutPayorPayee,
        _startDate, _endDate, _showBookmarked, _showCash,
        _showWithReceipts, _showExcludedFromCashFlow, _allTags
    ) { args ->
        val list = args[0] as Transactions
        val query = args[1] as String
        val tab = args[2] as String
        val type = args[3] as String
        val withNotes = args[4] as Boolean
        val withoutPayor = args[5] as Boolean
        val start = args[6] as Long?
        val end = args[7] as Long?
        val bookmarkedOnly = args[8] as Boolean
        val cashOnly = args[9] as Boolean
        val receiptsOnly = args[10] as Boolean
        val excludedOnly = args[11] as Boolean
        @Suppress("UNCHECKED_CAST")
        val tags = args[12] as List<Tag>

        list.transactions.filter { transaction ->
            val tagNames = tags.filter { transaction.tagIds.contains(it.id) }.map { it.name }
            val partyName = if (transaction.type == TransactionType.INCOME) transaction.senderName else transaction.receiverName
            val matchesQuery = partyName.contains(query, ignoreCase = true) ||
                    transaction.bank.contains(query, ignoreCase = true) ||
                    transaction.description.contains(query, ignoreCase = true) ||
                    transaction.referenceNumber?.contains(query, ignoreCase = true) == true ||
                    transaction.amount.toString().contains(query) ||
                    tagNames.any { it.contains(query, ignoreCase = true) }

            val matchesTab = when (tab) {
                "By Tags" -> transaction.tagIds.isNotEmpty()
                else -> true
            }

            val matchesType = when (type) {
                "Outgoing" -> transaction.type == TransactionType.EXPENSE
                "Incoming" -> transaction.type == TransactionType.INCOME
                else -> true
            }

            val matchesNotes = if (withNotes) transaction.description.isNotBlank() || !transaction.notes.isNullOrBlank() else true

            val matchesPayor = if (withoutPayor) {
                partyName.isBlank() && (transaction.contactName?.isBlank() != false)
            } else true

            val matchesDate = if (start != null && end != null) {
                transaction.transactionDate in start..end
            } else true

            val matchesBookmarked = if (bookmarkedOnly) transaction.isBookmarked else true
            val matchesCash = if (cashOnly) transaction.isCash else true
            val matchesReceipts = if (receiptsOnly) transaction.hasReceipt else true
            val matchesExcluded = if (excludedOnly) transaction.isExcludedFromCashFlow else true

            matchesQuery && matchesTab && matchesType && matchesNotes && matchesPayor && matchesDate &&
                    matchesBookmarked && matchesCash && matchesReceipts && matchesExcluded
        }
    }

    val groupedTransactions = filteredTransactions.map { list ->
        list.groupBy { transaction ->
            val date = Instant.ofEpochMilli(transaction.transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
            date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
        }.mapValues { Transactions(it.value) }
    }

    val transaction = combine(_allTransactions, _transactionId) { list, id ->
        list.transactions.find { it.id == id }
    }

    val activePartyTransactions = combine(_allTransactions, transaction) { list, active ->
        if (active == null) emptyList()
        else list.transactions.filter {
            if(it.type == TransactionType.INCOME)
                it.senderName.equals(active.senderName, ignoreCase = true)
            else it.receiverName.equals(active.receiverName, ignoreCase = true)
        }
    }

    val partyActivityDates = activePartyTransactions.map { list ->
        list.map { txn ->
            Instant.ofEpochMilli(txn.transactionDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.distinct().sorted()
    }

    val detailedFilteredTransactions = combine(activePartyTransactions, _selectedPartyDate) { list, date ->
        if (date == null) list
        else {
            val selectedLocalDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
            list.filter {
                val txnLocalDate = Instant.ofEpochMilli(it.transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
                txnLocalDate == selectedLocalDate
            }
        }
    }

    val detailedHeaderAmount = combine(detailedFilteredTransactions, activePartyTransactions, _selectedPartyDate) { filtered, all, date ->
        if (date == null) all.sumOf { it.amount }
        else filtered.sumOf { it.amount }
    }

    val partyInsight = combine(activePartyTransactions, _allTransactions, _allBudgets) { partyTxns, allTxns, allBudgets ->
        if (partyTxns.isEmpty()) return@combine null

        // Find the dominant category for this party based on spend amount
        val dominantEntry = partyTxns.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId ?: it.category }
            .mapValues { entry -> entry.value.fold(0.0) { acc, txn -> acc + abs(txn.amount) } }
            .maxByOrNull { it.value } ?: return@combine null

        val targetKey = dominantEntry.key
        val targetTransaction = partyTxns.find { (it.categoryId ?: it.category) == targetKey } ?: return@combine null
        val targetCategoryId = targetTransaction.categoryId
        val targetCategoryName = targetTransaction.category

        if (targetCategoryName == "Uncategorized") return@combine null

        // Total spent in this category across ALL transactions
        val totalSpentInCategory = allTxns.transactions.filter { it.type == TransactionType.EXPENSE }
            .filter { (it.categoryId != null && it.categoryId == targetCategoryId) || it.category.equals(targetCategoryName, true) }
            .sumOf { abs(it.amount) }

        // Total allotted for this category in ALL budgets
        val totalAllotted = allBudgets.flatMap { it.allocations }
            .filter { (targetCategoryId != null && it.categoryId == targetCategoryId) || it.category?.title?.equals(targetCategoryName, true) == true }
            .sumOf { it.allocatedAmount }

        if (totalAllotted > 0) {
            val percentage = ((totalSpentInCategory / totalAllotted) * 100).toInt()
            targetCategoryName to percentage
        } else null
    }

    fun getBudgetName(id: String?): String? = allBudgets.value.find { it.id == id }?.title
    fun getTagName(id: String?): String? = allTags.value.find { it.id == id }?.name
    fun getCategoryName(budgetId: String?, categoryId: String?): String? {
        val budget = allBudgets.value.find { it.id == budgetId }
        return budget?.allocations?.find { it.categoryId == categoryId }?.category?.title
    }

    val availableBudgets = combine(_allBudgets, _selectedBudgetType) { budgets, type ->
        if (type == null) emptyList()
        else budgets.filter { it.budgetType == type }
    }

    val availableCategories = combine(availableBudgets, _selectedBudgetId) { budgets, budgetId ->
        val budget = budgets.find { it.id == budgetId }
        budget?.allocations?.mapNotNull { it.category } ?: emptyList()
    }

    val availableDateRange = _allTransactions.map { list ->
        if (list.transactions.isEmpty()) {
            val now = System.currentTimeMillis()
            Pair(now, now)
        } else {
            val min = list.transactions.minOf { it.transactionDate }
            val max = list.transactions.maxOf { it.transactionDate }
            Pair(min, max)
        }
    }


    fun updateAllTransactions(list: Transactions) { _allTransactions.value = list }
    fun updateAllBudgets(list: List<Budget>) { _allBudgets.value = list }
    fun updateAllTags(list: List<Tag>) { _allTags.value = list }
    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateSelectedTab(tab: String) { _selectedTab.value = tab }
    fun updateSyncStatus(status: SyncStatus) { _syncStatus.value = status }

    fun updateTypeFilter(value: String) { _typeFilter.value = value }
    fun updateShowBookmarked(value: Boolean) { _showBookmarked.value = value }
    fun updateShowCash(value: Boolean) { _showCash.value = value }
    fun updateShowWithNotes(value: Boolean) { _showWithNotes.value = value }
    fun updateShowWithReceipts(value: Boolean) { _showWithReceipts.value = value }
    fun updateShowWithoutPayorPayee(value: Boolean) { _showWithoutPayorPayee.value = value }
    fun updateShowExcludedFromCashFlow(value: Boolean) { _showExcludedFromCashFlow.value = value }

    fun updateDateRange(start: Long?, end: Long?) {
        _startDate.value = start
        _endDate.value = end
    }

    fun updateSelectedPartyDate(value: Long?) {
        _selectedPartyDate.value = value
    }

    fun updateTransactionId(id: String?, fromNotification: Boolean = false) {
        val newId = if (id.isNullOrBlank()) null else id
        if (newId == null) {
            clearCreationDraft()
        } else if (_transactionId.value != newId) {
            _transactionId.value = newId
            _isFromNotification.value = fromNotification
        } else {
            _isFromNotification.value = fromNotification
        }
    }

    fun updateBudgetType(type: BudgetType?) { 
        _selectedBudgetType.value = type
        _selectedBudgetId.value = null
        _selectedCategoryId.value = null
    }
    fun updateBudgetId(id: String?) { 
        _selectedBudgetId.value = id
        _selectedParentCategoryId.value = null
        _selectedCategoryId.value = null
    }

    fun updateParentCategoryId(id: String?) {
        _selectedParentCategoryId.value = id
        _selectedCategoryId.value = null
    }

    fun updateCategoryId(id: String?) { _selectedCategoryId.value = id }
    fun updateTransactionType(type: TransactionType?) { _selectedTransactionType.value = type }
    fun updateNotes(value: String) { _notes.value = value }
    fun updateSenderName(value: String) { _senderName.value = value }
    fun updateReceiverName(value: String) { _receiverName.value = value }
    fun updateContactName(value: String) { _contactName.value = value }
    fun updateAmount(value: String) { _amount.value = value }
    fun updateBank(value: String) { _bank.value = value }
    fun updateMode(value: String) { _mode.value = value }
    fun updateDate(value: Long) { _date.value = value }
    fun updateIsBookmarked(value: Boolean) { _isBookmarked.value = value }
    fun updateIsCash(value: Boolean) { _isCash.value = value }
    fun updateHasReceipt(value: Boolean) { _hasReceipt.value = value }
    fun updateIsExcludedFromCashFlow(value: Boolean) { _isExcludedFromCashFlow.value = value }

    fun onTagToggle(tagId: String) {
        _selectedTagIds.update { if (it.contains(tagId)) it - tagId else it + tagId }
    }

    fun onTagAdd(tagId: String) {
        _selectedTagIds.update { if (it.contains(tagId)) it else it + tagId }
    }

    fun updateLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun clear() {
        _allTransactions.value = Transactions(transactions = emptyList())
        _allBudgets.value = emptyList()
        _allTags.value = emptyList()
        _searchQuery.value = ""
        _selectedTab.value = "All"
        _syncStatus.value = SyncStatus.Idle
        _typeFilter.value = "Both"
        _showBookmarked.value = false
        _showCash.value = false
        _showWithNotes.value = false
        _showWithReceipts.value = false
        _showWithoutPayorPayee.value = false
        _showExcludedFromCashFlow.value = false
        _startDate.value = null
        _endDate.value = null
        _isLoading.value = false
        _selectedPartyDate.value = null
        clearCreationDraft()
    }

    fun clearCreationDraft() {
        _transactionId.value = null
        _selectedBudgetType.value = null
        _selectedBudgetId.value = null
        _selectedParentCategoryId.value = null
        _selectedCategoryId.value = null
        _selectedTagIds.value = emptyList()
        _notes.value = ""
        _selectedTransactionType.value = null
        _senderName.value = ""
        _receiverName.value = ""
        _contactName.value = ""
        _amount.value = ""
        _bank.value = ""
        _mode.value = ""
        _date.value = System.currentTimeMillis()
        _isBookmarked.value = false
        _isCash.value = false
        _hasReceipt.value = false
        _isExcludedFromCashFlow.value = false
        _isFromNotification.value = false
    }

    fun clearNotificationFlag() {
        _isFromNotification.value = false
    }

    fun prepopulate(txn: Transaction) {
        _selectedBudgetId.value = txn.budgetId
        _selectedCategoryId.value = txn.categoryId
        _selectedTagIds.value = txn.tagIds
        _notes.value = txn.notes ?: ""
        _selectedTransactionType.value = txn.type
        _senderName.value = txn.senderName
        _receiverName.value = txn.receiverName
        _contactName.value = txn.contactName ?: ""
        _amount.value = txn.amount.toString()
        _bank.value = txn.bank
        _mode.value = txn.mode ?: ""
        _date.value = txn.transactionDate
        _isBookmarked.value = txn.isBookmarked
        _isCash.value = txn.isCash
        _hasReceipt.value = txn.hasReceipt
        _isExcludedFromCashFlow.value = txn.isExcludedFromCashFlow
    }


    fun classify() {
        _classifyIntent.tryEmit(Unit)
    }

}
