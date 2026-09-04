package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.math.abs

class TagsFluxDeck(
    userPreferences: UserPreferences,
) {
    private val _saveTagIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saveTagIntent = _saveTagIntent.asSharedFlow()

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags = _allTags.asStateFlow()

    private val _allTransactions = MutableStateFlow(Transactions(emptyList()))
    private val _allCategories = MutableStateFlow<List<Category>>(emptyList())

    private val _allBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val allBudgets = _allBudgets.asStateFlow()

    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())
    val allGoals = _allGoals.asStateFlow()

    val hiddenTagIds = userPreferences.hiddenTagIds

    private val _tagName = MutableStateFlow("")
    val tagName = _tagName.asStateFlow()

    private val _tagBudgetGoal = MutableStateFlow("")
    val tagBudgetGoal = _tagBudgetGoal.asStateFlow()

    private val _tagColor = MutableStateFlow("0xFF4CAF50")
    val tagColor = _tagColor.asStateFlow()

    private val _lastCreatedTagId = MutableStateFlow<String?>(null)
    val lastCreatedTagId = _lastCreatedTagId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedPeriod = MutableStateFlow("All")
    val selectedPeriod = _selectedPeriod.asStateFlow()

    private val _clickedTagId = MutableStateFlow("")

    private val _showHidden = MutableStateFlow(false)
    val showHidden = _showHidden.asStateFlow()

    private val _excludedTagIds = MutableStateFlow<Set<String>>(emptySet())

    val filteredTags = combine(
        _allTags, _allTransactions, hiddenTagIds, _searchQuery, _showHidden, _excludedTagIds, _allGoals, _allCategories, _selectedPeriod
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val tags = args[0] as List<Tag>
        @Suppress("UNCHECKED_CAST")
        val transactions = args[1] as Transactions
        @Suppress("UNCHECKED_CAST")
        val hIds = args[2] as Set<String>
        val query = args[3] as String
        val sHidden = args[4] as Boolean
        @Suppress("UNCHECKED_CAST")
        val eIds = args[5] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val goals = args[6] as List<Goal>
        @Suppress("UNCHECKED_CAST")
        val categories = args[7] as List<Category>
        val period = args[8] as String

        tags.filter { tag ->
            if (eIds.contains(tag.id)) return@filter false
            val isHidden = hIds.contains(tag.id)
            if (sHidden) {
                if (!isHidden) return@filter false
            } else {
                if (isHidden) return@filter false
            }

            tag.name.contains(query, ignoreCase = true)
        }.map { tag ->
            val allTagTransactions = transactions.transactions.filter { it.tagIds.contains(tag.id) }
            val tagTransactions = filterTransactionsListByPeriod(allTagTransactions, period)

            val allTagGoals = goals.filter { it.tagId == tag.id }
            val tagGoals = filterGoalsListByPeriod(allTagGoals, period)

            val spent = tagTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) } + tagGoals.sumOf { it.savedAmount }
            val income = tagTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

            val allPossibleCats = allPossibleCategories(categories)
            val associatedCats = tagTransactions.mapNotNull { txn ->
                allPossibleCats.find { it.id == txn.categoryId }
                    ?: allPossibleCats.find { it.title.equals(txn.category, true) }
            }.distinctBy { it.id }

            TagSummary(
                tag = tag,
                transactionCount = tagTransactions.size,
                goalCount = tagGoals.size,
                totalSpent = spent,
                totalIncoming = income,
                associatedCategories = associatedCats,
                associatedGoals = tagGoals,
                insightText = calculateInsight(Transactions(tagTransactions), tagGoals, tag.budgetGoal),
            )
        }
    }

    val detailAnalytics = combine(
        _allTags, _allTransactions, _allBudgets, _allCategories, _clickedTagId, _selectedPeriod, _allGoals
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val tags = args[0] as List<Tag>
        @Suppress("UNCHECKED_CAST")
        val transactions = args[1] as Transactions
        @Suppress("UNCHECKED_CAST")
        val budgets = args[2] as List<Budget>
        @Suppress("UNCHECKED_CAST")
        val categories = args[3] as List<Category>
        val id = args[4] as String
        val period = args[5] as String
        @Suppress("UNCHECKED_CAST")
        val goals = args[6] as List<Goal>

        val tag = tags.find { it.id == id } ?: return@combine null

        val allTagTransactions = transactions.transactions.filter { it.tagIds.contains(tag.id) }
        val filteredTxs = filterByPeriod(Transactions(allTagTransactions), period)

        val allTagGoals = goals.filter { it.tagId == tag.id }
        val tagGoals = filterGoalsListByPeriod(allTagGoals, period)
        val goalsSaved = tagGoals.sumOf { it.savedAmount }
        val goalsTarget = tagGoals.sumOf { it.targetAmount }

        val totalSpent = filteredTxs.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) } + goalsSaved

        val allPossibleCats = allPossibleCategories(categories)

        val budgetIds = allTagTransactions.mapNotNull { it.budgetId }.distinct()
        val relevantBudgets = budgets.filter { budgetIds.contains(it.id) }

        val rootIds = allTagTransactions.mapNotNull { txn ->
            val c = allPossibleCats.find { it.id == txn.categoryId }
                ?: allPossibleCats.find { it.title.equals(txn.category, true) }
            c?.parentCategoryId ?: c?.id
        }.distinct()

        val categoryAnalytics = rootIds.mapNotNull { rootId ->
            val rootCat = allPossibleCats.find { it.id == rootId } ?: return@mapNotNull null
            if (rootCat.parentCategoryId != null) return@mapNotNull null

            val catSpent = filteredTxs.transactions.filter { txn ->
                val c = allPossibleCats.find { it.id == txn.categoryId }
                    ?: allPossibleCats.find { it.title.equals(txn.category, true) }
                (c?.parentCategoryId ?: c?.id) == rootId
            }.sumOf { abs(it.amount) }
            val catAllotted = relevantBudgets.sumOf { b ->
                b.allocations.find { it.categoryId == rootId }?.allocatedAmount ?: 0.0
            }

            if (catSpent == 0.0 && catAllotted == 0.0) return@mapNotNull null

            TagCategoryAnalytics(
                category = rootCat,
                spent = catSpent,
                allotted = catAllotted,
                percentage = if (catAllotted > 0) (catSpent / catAllotted * 100).toInt() else 0,
            )
        }

        val totalAllotted = if (tag.budgetGoal > 0) tag.budgetGoal else (categoryAnalytics.sumOf { it.allotted } + goalsTarget)

        TagDetailAnalytics(
            tag = tag,
            totalSpent = totalSpent,
            transactionCount = filteredTxs.transactions.size,
            remainingToSave = (totalAllotted - totalSpent).coerceAtLeast(0.0),
            recommendedPerDay = if (totalAllotted > 0) (totalAllotted / 30.0) else 0.0,
            progress = if (totalAllotted > 0) (totalSpent / totalAllotted).toFloat().coerceIn(0f, 1f) else 0f,
            categories = categoryAnalytics,
            totalAllotted = totalAllotted,
            filteredTransactions = filteredTxs,
            goals = tagGoals,
        )
    }

    private fun filterByPeriod(transactions: Transactions, period: String): Transactions {
        return Transactions(filterTransactionsListByPeriod(transactions.transactions, period))
    }

    private fun filterTransactionsListByPeriod(list: List<Transaction>, period: String): List<Transaction> {
        if (period == "All" || period.isBlank()) return list
        val now = LocalDate.now()
        return list.filter {
            val date = Instant.ofEpochMilli(it.transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
            when (period) {
                "This Month" -> date.month == now.month && date.year == now.year
                "This Week" -> !date.isBefore(now.minusDays(7))
                "Last 3 Months" -> !date.isBefore(now.minusMonths(3))
                "Last Month" -> date.month == now.minusMonths(1).month && date.year == now.minusMonths(1).year
                "This Year" -> date.year == now.year
                else -> true
            }
        }
    }

    private fun filterGoalsListByPeriod(list: List<Goal>, period: String): List<Goal> {
        if (period == "All" || period.isBlank()) return list
        val now = LocalDate.now()
        return list.filter {
            val date = it.creationDate.parseIsoToLocalDate() ?: now
            when (period) {
                "This Month" -> date.month == now.month && date.year == now.year
                "This Week" -> !date.isBefore(now.minusDays(7))
                "Last 3 Months" -> !date.isBefore(now.minusMonths(3))
                "Last Month" -> date.month == now.minusMonths(1).month && date.year == now.minusMonths(1).year
                "This Year" -> date.year == now.year
                else -> true
            }
        }
    }

    private fun calculateInsight(transactions: Transactions, goals: List<Goal>, goal: Double): String {
        val spent = transactions.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) } + goals.sumOf { it.savedAmount }
        if (transactions.transactions.isEmpty() && goals.isEmpty()) return "No activity found for this tag."
        return when {
            goal <= 0 -> ""
            spent > goal -> "You have exceeded your budget goal by ${String.format(Locale.getDefault(), "%.2f", spent - goal)}."
            spent > goal * 0.8 -> "You are close to your budget goal. Spend wisely!"
            else -> "You are well within your budget goal."
        }
    }

    fun getSpentAmountForCategory(categoryId: String): Flow<Double> = combine(
        detailAnalytics, _allCategories
    ) { analytics, allCats ->
        if (analytics == null) return@combine 0.0

        val targetCat = allCats.find { it.id == categoryId } ?: return@combine 0.0
        val isRoot = targetCat.parentCategoryId == null

        val children = if (isRoot) allCats.filter { it.parentCategoryId == categoryId } else emptyList()
        val targetIds = (listOf(targetCat) + children).map { it.id }
        val targetNames = (listOf(targetCat) + children).map { it.title }

        analytics.filteredTransactions.transactions.filter { it.type == TransactionType.EXPENSE }.filter { txn ->
            val resolvedCat = (allCats + defaultDialogCategories + defaultSubCategories).distinctBy { it.id }.find { it.id == txn.categoryId }
                ?: (allCats + defaultDialogCategories + defaultSubCategories).distinctBy { it.id }.find { it.title.equals(txn.category, true) }

            val resolvedId = resolvedCat?.id
            val resolvedName = resolvedCat?.title ?: txn.category

            (resolvedId != null && targetIds.contains(resolvedId)) || targetNames.any { it.equals(resolvedName, true) }
        }.sumOf { abs(it.amount) }
    }

    fun getTransactionsForCategory(categoryId: String): Flow<Transactions> = combine(
        detailAnalytics, _allCategories
    ) { analytics, allCats ->
        if (analytics == null) return@combine Transactions(emptyList())

        val targetCat = allCats.find { it.id == categoryId } ?: return@combine Transactions(emptyList())
        val isRoot = targetCat.parentCategoryId == null

        val children = if (isRoot) allCats.filter { it.parentCategoryId == categoryId } else emptyList()
        val targetIds = (listOf(targetCat) + children).map { it.id }
        val targetNames = (listOf(targetCat) + children).map { it.title }

        val analysis = analytics.filteredTransactions.transactions.filter { it.type == TransactionType.EXPENSE }.filter { txn ->
            val resolvedCat = (allCats + defaultDialogCategories + defaultSubCategories).distinctBy { it.id }.find { it.id == txn.categoryId }
                ?: (allCats + defaultDialogCategories + defaultSubCategories).distinctBy { it.id }.find { it.title.equals(txn.category, true) }

            val resolvedId = resolvedCat?.id
            val resolvedName = resolvedCat?.title ?: txn.category

            (resolvedId != null && targetIds.contains(resolvedId)) || targetNames.any { it.equals(resolvedName, true) }
        }

        Transactions(analysis)
    }

    private fun allPossibleCategories(categories: List<Category>) =
        (categories + defaultDialogCategories + defaultSubCategories).distinctBy { it.id }

    fun updateAllTags(list: List<Tag>) { _allTags.value = list }
    fun updateAllTransactions(list: Transactions) { _allTransactions.value = list }
    fun updateAllBudgets(list: List<Budget>) { _allBudgets.value = list }
    fun updateAllCategories(list: List<Category>) { _allCategories.value = list }
    fun updateAllGoals(list: List<Goal>) { _allGoals.value = list }

    fun toggleShowHidden() { _showHidden.value = !_showHidden.value }
    fun updateTagName(value: String) { _tagName.value = value.take(20) }
    fun updateTagBudgetGoal(value: String) { _tagBudgetGoal.value = value }
    fun updateTagColor(value: String) { _tagColor.value = value }
    fun updateSearchQuery(value: String) { _searchQuery.value = value }
    fun updateSelectedPeriod(value: String) { _selectedPeriod.value = value }
    fun updateClickedTagId(value: String) { _clickedTagId.value = value }
    fun updateLastCreatedTagId(value: String?) { _lastCreatedTagId.value = value }
    fun updateLoading(value: Boolean) { _isLoading.value = value }

    fun clear() {
        _allTags.value = emptyList()
        _allTransactions.value = Transactions(emptyList())
        _allCategories.value = emptyList()
        _allBudgets.value = emptyList()
        _allGoals.value = emptyList()
        _lastCreatedTagId.value = null
        _isLoading.value = false
        _searchQuery.value = ""
        _selectedPeriod.value = "All"
        _clickedTagId.value = ""
        _showHidden.value = false
        _excludedTagIds.value = emptySet()
        clearForm()
    }

    fun clearForm() {
        _tagName.value = ""
        _tagBudgetGoal.value = ""
        _tagColor.value = "0xFF4CAF50"
    }

    fun save() {
        _saveTagIntent.tryEmit(Unit)
    }
}
