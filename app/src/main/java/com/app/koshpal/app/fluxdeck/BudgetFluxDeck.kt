package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import com.app.koshpal.core.presentation.util.toIso8601String
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.flow.*

class BudgetFluxDeck(
    userPreferences: UserPreferences
) {

    private fun parseBudgetDate(dateStr: String): LocalDate? {
        return dateStr.parseIsoToLocalDate()
    }

    private val _allBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val allBudgets = _allBudgets.asStateFlow()

    private val _archivedBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val archivedBudgets = _archivedBudgets.asStateFlow()

    private val _mainCategories = MutableStateFlow<List<Category>>(emptyList())

    private val _spentMap = MutableStateFlow<Map<String, Double>>(emptyMap())
    private val _categorySpentMap = MutableStateFlow<Map<String, Double>>(emptyMap())

    val hiddenBudgetIds = userPreferences.hiddenBudgetIds
    val flaggedBudgetIds = userPreferences.flaggedBudgetIds
    val hiddenCategoryIds = userPreferences.hiddenCategoryIds

    val flaggedBudgets: Flow<List<Budget>> = combine(_allBudgets, flaggedBudgetIds) { budgets, ids ->
        budgets.filter { ids.contains(it.id) }
    }

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _manualAmountOverride = MutableStateFlow<String?>(null)
    
    private val _period = MutableStateFlow(BudgetPeriod.Monthly)
    val period = _period.asStateFlow()

    private val _startDate = MutableStateFlow("Select a date")
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow("Select a date")
    val endDate = _endDate.asStateFlow()

    private val _budgetType = MutableStateFlow(BudgetType.RECURRING)
    val budgetType = _budgetType.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    private val _manualAmounts = MutableStateFlow<Map<String, String>>(emptyMap())

    private val _lastCreatedBudgetId = MutableStateFlow<String?>(null)
    val lastCreatedBudgetId = _lastCreatedBudgetId.asStateFlow()

    private val _createBudgetIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val createBudgetIntent = _createBudgetIntent.asSharedFlow()

    private val _saveCategoryIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saveCategoryIntent = _saveCategoryIntent.asSharedFlow()

    private val _createSubCategoryIntent = MutableSharedFlow<Category?>(extraBufferCapacity = 1)
    val createSubCategoryIntent = _createSubCategoryIntent.asSharedFlow()


    private val _saveBudgetChangesIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saveBudgetChangesIntent = _saveBudgetChangesIntent.asSharedFlow()

    private val _isItemClicked = MutableStateFlow(false)
    val isItemClicked = _isItemClicked.asStateFlow()

    private val _clickedBudgetId = MutableStateFlow("")
    val clickedBudgetId = _clickedBudgetId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _budgetTypeFilter = MutableStateFlow("all")
    val budgetTypeFilter = _budgetTypeFilter.asStateFlow()

    private val _filterPeriod = MutableStateFlow<BudgetPeriod?>(null)
    val filterPeriod = _filterPeriod.asStateFlow()

    private val _filterDate = MutableStateFlow<LocalDate?>(null)
    val filterDate = _filterDate.asStateFlow()

    private val _showHidden = MutableStateFlow(false)
    val showHidden = _showHidden.asStateFlow()

    private val _showHistory = MutableStateFlow(false)
    val showHistory = _showHistory.asStateFlow()

    private val _isCreatingCategoryInSheet = MutableStateFlow(false)
    val isCreatingCategoryInSheet = _isCreatingCategoryInSheet.asStateFlow()

    private val _creationStep = MutableStateFlow(1)
    val creationStep = _creationStep.asStateFlow()

    private val _excludedBudgetIds = MutableStateFlow<Set<String>>(emptySet())

    private val _excludedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    val excludedCategoryIds = _excludedCategoryIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val showZeroAmountAlert = MutableStateFlow(false)

    val allocations: Flow<List<CategoryAllocationUiState>> = combine(_categories, _manualAmounts) { cats, amounts ->
        cats.map { cat ->
            val manual = amounts[cat.id]
            val amountStr = if (!manual.isNullOrBlank()) manual else {
                val subSum = cats.filter { it.parentCategoryId == cat.id }.sumOf { amounts[it.id]?.toDoubleOrNull() ?: 0.0 }
                if (subSum > 0) subSum.toCleanString() else ""
            }
            CategoryAllocationUiState(category = cat, amountString = amountStr)
        }
    }

    val totalCategorySum = allocations.map { list ->
        list.filter { it.category.parentCategoryId == null }.sumOf { it.amountDouble }
    }

    val overallAmountString = combine(_manualAmountOverride, totalCategorySum) { manual, sum ->
        if (!manual.isNullOrBlank()) manual else if (sum > 0) sum.toCleanString() else ""
    }

    val overAllocatedAmount = combine(overallAmountString, totalCategorySum, _manualAmountOverride) { overallStr, catSum, manual ->
        val overall = overallStr.toDoubleOrNull() ?: 0.0
        val isManual = !manual.isNullOrBlank()
        if (isManual && overall > 0 && catSum > overall) catSum - overall else 0.0
    }

    val budgetsWithSpent: Flow<List<Pair<Budget, Double>>> = combine(_allBudgets, _spentMap) { list, spentMap ->
        list.map { b -> b to (spentMap[b.id] ?: 0.0) }
    }

    val historyBudgetsWithSpent: Flow<List<Pair<Budget, Double>>> = combine(_archivedBudgets, _spentMap) { list, spentMap ->
        list.map { b -> b to (spentMap[b.id] ?: 0.0) }
    }

    val filteredBudgets = getFilteredBudgetsFlow(budgetsWithSpent)
    val filteredHistoryBudgets = getFilteredBudgetsFlow(historyBudgetsWithSpent)

    val totalBudgetedAmount = _allBudgets.map { it.sumOf { b -> b.amount } }

    val titleSuggestions: Flow<List<String>> = _allBudgets.map { list ->
        val mostUsed = list.groupBy { it.title }.mapValues { it.value.size }.toList().sortedByDescending { it.second }.take(3).map { it.first }
        if (mostUsed.size < 3) {
            val defaults = listOf("Monthly Budget", "Groceries", "Emergency Fund", "Vacation", "Entertainment")
            (mostUsed + defaults).distinct().take(3)
        } else mostUsed
    }

    fun isAnySelectedFlagged(selectedIds: Flow<Set<String>>, flaggedIds: Flow<Set<String>>): Flow<Boolean> =
        combine(selectedIds, flaggedIds) { selected, flagged -> selected.any { flagged.contains(it) } }

    fun isAnySelectedHidden(
        isItemClicked: Flow<Boolean>,
        selectedItems: Flow<Set<String>>,
        selectedCategories: Flow<Set<String>>,
        hiddenBudgets: Flow<Set<String>>,
        hiddenCategories: Flow<Set<String>>
    ): Flow<Boolean> = combine(isItemClicked, selectedItems, selectedCategories, hiddenBudgets, hiddenCategories) { clicked, items, categories, hBudgets, hCats ->
        if (clicked) categories.any { hCats.contains(it) } else items.any { hBudgets.contains(it) }
    }

    fun getSpentAmountForBudget(budgetId: String): Flow<Double> = _spentMap.map { it[budgetId] ?: 0.0 }
    fun getSpentAmountForCategory(categoryId: String): Flow<Double> = _categorySpentMap.map { it[categoryId] ?: 0.0 }

    private fun getFilteredBudgetsFlow(source: Flow<List<Pair<Budget, Double>>>): Flow<List<Budget>> = combine(
        source, _searchQuery, _budgetTypeFilter, _filterPeriod, _filterDate, hiddenBudgetIds, _showHidden, _excludedBudgetIds, flaggedBudgetIds
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val list = args[0] as List<Pair<Budget, Double>>
        val query = args[1] as String
        val type = args[2] as String
        val period = args[3] as BudgetPeriod?
        val date = args[4] as LocalDate?
        @Suppress("UNCHECKED_CAST")
        val hIds = args[5] as Set<String>
        val sHidden = args[6] as Boolean
        @Suppress("UNCHECKED_CAST")
        val eIds = args[7] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val fIds = args[8] as Set<String>

        val tokens = query.lowercase().split(" ").filter { it.isNotBlank() }
        val isOverBudgetQuery = query.lowercase().contains("over budget") || type == "over_budget"

        val filtered = list.filter { (budget, spent) ->
            if (eIds.contains(budget.id)) return@filter false
            val isHidden = hIds.contains(budget.id)
            if (isHidden != sHidden) return@filter false
            
            val matchesType = type == "all" || type == "over_budget" || budget.budgetType.name.equals(type, ignoreCase = true)
            val matchesPeriod = period == null || budget.period == period
            
            val budgetDate = parseBudgetDate(budget.startDate)
            val matchesDate = date == null || (budgetDate?.month == date.month && budgetDate.year == date.year)
            
            if (!matchesType || !matchesPeriod || !matchesDate) return@filter false

            if (tokens.isEmpty()) return@filter true

            if (isOverBudgetQuery && spent <= budget.amount) return@filter false

            val searchTokens = if (isOverBudgetQuery) tokens.filter { it != "over" && it != "budget" } else tokens
            
            searchTokens.all { token ->
                val titleMatch = budget.title.contains(token, ignoreCase = true)
                val categoryMatch = budget.allocations.any { it.category?.title?.contains(token, ignoreCase = true) == true }
                val periodMatch = budget.period.name.contains(token, ignoreCase = true)
                
                val monthName = budgetDate?.month?.name?.lowercase() ?: ""
                val monthShort = monthName.take(3)
                val monthMatch = monthName.contains(token) || monthShort.contains(token) || budgetDate?.year?.toString()?.contains(token) == true
                
                titleMatch || categoryMatch || periodMatch || monthMatch
            }
        }

        filtered.sortedWith(
            compareByDescending<Pair<Budget, Double>> { (budget, _) -> fIds.contains(budget.id) }
                .thenByDescending { (budget, _) -> budget.startDate }
        ).map { it.first }
    }

    private val _categoryName = MutableStateFlow("")
    val categoryName = _categoryName.asStateFlow()

    private val _categoryIcon = MutableStateFlow("none")
    val categoryIcon = _categoryIcon.asStateFlow()

    private val _categoryColor = MutableStateFlow(availableCategoryColors.first())
    val categoryColor = _categoryColor.asStateFlow()

    private val _subCategoryDrafts = MutableStateFlow<List<CategoryAllocationUiState>>(emptyList())
    val subCategoryDrafts = _subCategoryDrafts.asStateFlow()

    private val _subCategoryName = MutableStateFlow("")
    val subCategoryName = _subCategoryName.asStateFlow()

    private val _subCategoryIcon = MutableStateFlow("none")
    val subCategoryIcon = _subCategoryIcon.asStateFlow()

    private val _subCategoryColor = MutableStateFlow(availableCategoryColors.first())
    val subCategoryColor = _subCategoryColor.asStateFlow()

    private val _parentCategoryId = MutableStateFlow<String?>(null)
    val parentCategoryId = _parentCategoryId.asStateFlow()

    private val _editingCategoryId = MutableStateFlow<String?>(null)
    val editingCategory: Flow<CategoryAllocationUiState?> = combine(_editingCategoryId, allocations) { id, list ->
        if (id == null) null else list.find { it.category.id == id }
    }

    val buildBudgetDraft: Flow<Budget> =
        combine(
            title,
            overallAmountString,
            period,
            startDate,
            endDate,
            budgetType,
            allocations,
        ) { args ->
            val budgetTitle = args[0] as String
            val amountStr = args[1] as String
            val budgetPeriod = args[2] as BudgetPeriod
            val start = args[3] as String
            val end = args[4] as String
            val type = args[5] as BudgetType
            @Suppress("UNCHECKED_CAST")
            val uiAllocations = args[6] as List<CategoryAllocationUiState>

            val budgetId = clickedBudgetId.value.ifBlank { "" }
            val entityAllocations = uiAllocations.map { it.toEntity(budgetId) }

            Budget(
                title = budgetTitle, 
                amount = amountStr.toDoubleOrNull() ?: 0.0, 
                period = budgetPeriod, 
                startDate = start, 
                endDate = end, 
                budgetType = type, 
                allocations = entityAllocations
            )
        }
    val buildCategoryDraft: Flow<Category> =
        combine(
            categoryName,
            categoryIcon,
            categoryColor
        ) { name, icon, color ->
            Category(title = name, iconResId = icon, colorHex = color)
        }
    val buildSubCategoryDraft = combine(
        subCategoryName,
        subCategoryIcon,
        subCategoryColor,
        parentCategoryId
    ) { name, icon, color, parentId ->
        Category(title = name, iconResId = icon, colorHex = color, parentCategoryId = parentId)
    }

    fun saveBudgetChanges() {
        _saveBudgetChangesIntent.tryEmit(Unit)
    }

    val searchSuggestions = flowOf(listOf("Over Budget", "Monthly", "Yearly", "Groceries", "Bills", "Rent"))

    fun updateAllBudgets(list: List<Budget>) { _allBudgets.value = list }
    fun updateArchivedBudgets(list: List<Budget>) { _archivedBudgets.value = list }
    fun updateMainCategories(list: List<Category>) { _mainCategories.value = list }
    fun updateSpentMap(map: Map<String, Double>) { _spentMap.value = map }
    fun updateCategorySpentMap(map: Map<String, Double>) { _categorySpentMap.value = map }

    fun updateTitle(value: String) { _title.value = value.take(20) }
    fun updateOverallAmount(value: String) { _manualAmountOverride.value = value.ifBlank { null }
    }
    fun updatePeriod(value: BudgetPeriod) { _period.value = value }
    fun updateStartDate(value: String) { _startDate.value = value }
    fun updateEndDate(value: String) { _endDate.value = value }
    fun updateBudgetType(value: BudgetType) {
        if (_budgetType.value != value) {
            clearWorkflow()
            _budgetType.value = value
        }
    }
    fun updateCategoryAmount(categoryId: String, newAmountStr: String) {
        _manualAmounts.update { it + (categoryId to newAmountStr) }
        _subCategoryDrafts.update { list ->
            list.map { 
                if (it.category.id == categoryId) it.copy(amountString = newAmountStr) 
                else it 
            }
        }
    }

    fun createBudget() { _createBudgetIntent.tryEmit(Unit) }
    fun createCategory() { _saveCategoryIntent.tryEmit(Unit) }
    fun createSubCategory(preset: Category? = null) {
        _createSubCategoryIntent.tryEmit(preset)
    }


    fun addCategory(newCategory: Category) { if (_categories.value.none { it.id == newCategory.id }) _categories.update { it + newCategory } }
    
    fun addCategoryWithAmount(category: Category, amount: String) {
        addCategory(category)
        if (amount.isNotBlank()) {
            updateCategoryAmount(category.id, amount)
        }
    }

    fun removeCategory(category: Category) {
        _categories.update { it.filterNot { cat -> cat.id == category.id || cat.parentCategoryId == category.id } }
        _manualAmounts.update { it - category.id }
        _subCategoryDrafts.update { it.filterNot { item -> item.category.id == category.id } }
    }
    fun addSubCategoryToExisting(subCat: Category, parent: Category) {
        val linkedSubCat = subCat.copy(id = UUID.randomUUID().toString(), parentCategoryId = parent.id)
        if (_categories.value.none { it.title.equals(linkedSubCat.title, ignoreCase = true) && it.parentCategoryId == parent.id }) _categories.update { it + linkedSubCat }
    }
    fun removeSubCategoryFromExisting(subCatId: String) {
        _categories.update { it.filterNot { cat -> cat.id == subCatId } }
        _manualAmounts.update { it - subCatId }
    }
    fun updateCategoryName(value: String) { _categoryName.value = value.take(20) }
    fun updateCategoryIcon(value: String) { _categoryIcon.value = value }
    fun updateCategoryColor(value: String) {
        _categoryColor.value = value
        _subCategoryDrafts.update { list -> list.map { it.copy(category = it.category.copy(colorHex = value)) } }
    }
    fun updateSubCategoryName(value: String) { _subCategoryName.value = value.take(20) }
    fun updateSubCategoryIcon(value: String) { _subCategoryIcon.value = value }
    fun updateSubCategoryDrafts(value: List<CategoryAllocationUiState>) { _subCategoryDrafts.value = value }
    fun prepareSubCategoryFor(parentId: String, inheritedColor: String) {
        _subCategoryName.value = ""; _subCategoryIcon.value = "category"; _parentCategoryId.value = parentId; _subCategoryColor.value = inheritedColor
    }
    fun updateEditingCategoryId(value: String?) { _editingCategoryId.value = value }
    fun updateIsItemClicked(value: Boolean) { _isItemClicked.value = value }
    fun updateClickedBudgetId(value: String) { _clickedBudgetId.value = value }
    fun updateSearchQuery(value: String) { _searchQuery.value = value }
    fun updateBudgetTypeFilter(value: String) { _budgetTypeFilter.value = value }
    fun updateFilterPeriod(value: BudgetPeriod?) { _filterPeriod.value = value }
    fun updateFilterDate(value: LocalDate?) { _filterDate.value = value }
    fun toggleShowHidden() { _showHidden.value = !_showHidden.value }
    fun setShowHidden(value: Boolean) { _showHidden.value = value }
    fun toggleHistory() { _showHistory.value = !_showHistory.value }
    fun updateCreationStep(step: Int) { _creationStep.value = step }
    fun updateIsCreatingCategoryInSheet(value: Boolean) { _isCreatingCategoryInSheet.value = value }
    fun updateLastCreatedBudgetId(value: String?) { _lastCreatedBudgetId.value = value }
    fun updateLoading(value: Boolean) { _isLoading.value = value }
    fun excludeBudgetSelection(ids: List<String>) { _excludedBudgetIds.update { it + ids } }
    fun excludeCategorySelection(ids: List<String>) { _excludedCategoryIds.update { it + ids } }
    
    fun clear() {
        _allBudgets.value = emptyList()
        _archivedBudgets.value = emptyList()
        _mainCategories.value = emptyList()
        _spentMap.value = emptyMap()
        _categorySpentMap.value = emptyMap()
        _excludedBudgetIds.value = emptySet()
        _excludedCategoryIds.value = emptySet()
        _lastCreatedBudgetId.value = null
        _clickedBudgetId.value = ""
        _isItemClicked.value = false
        _searchQuery.value = ""
        _budgetTypeFilter.value = "all"
        _filterPeriod.value = null
        _filterDate.value = null
        _showHidden.value = false
        _showHistory.value = false
        clearWorkflow()
    }

    fun clearWorkflow() {
        _title.value = ""; _manualAmountOverride.value = null; _period.value = BudgetPeriod.Monthly; _startDate.value = "Select a date"; _endDate.value = "Select a date"
        _budgetType.value = BudgetType.RECURRING; _categories.value = emptyList(); _manualAmounts.value = emptyMap(); clearCategoryDraft()
    }
    fun clearCategoryDraft() {
        _categoryName.value = ""; _categoryIcon.value = "none"; _categoryColor.value = availableCategoryColors.first()
        _subCategoryDrafts.value = emptyList(); _subCategoryName.value = ""; _subCategoryIcon.value = "none"
        _subCategoryColor.value = availableCategoryColors.first(); _parentCategoryId.value = null
    }


    fun prepareClone(b: Budget) {
        val now = LocalDate.now()
        val originalStart = parseBudgetDate(b.startDate)
        val originalEnd = if (b.endDate != null && b.endDate != "Select a date") parseBudgetDate(b.endDate) else null
        
        val newEndStr = if (originalStart != null && originalEnd != null) {
            val duration = ChronoUnit.DAYS.between(originalStart, originalEnd)
            now.plusDays(duration).atStartOfDay(ZoneOffset.UTC).toInstant().toString()
        } else {
            "Select a date"
        }

        _title.value = b.title
        _manualAmountOverride.value = if (b.amount > 0) b.amount.toCleanString() else null
        _period.value = b.period
        _startDate.value = System.currentTimeMillis().toIso8601String()
        _endDate.value = newEndStr
        _budgetType.value = b.budgetType
        _categories.value = b.allocations.map { it.category ?: Category(id = it.categoryId, title = "Unknown", iconResId = "category", colorHex = "0xFF9E9E9E") }
        _manualAmounts.value = b.allocations.filter { it.allocatedAmount > 0 }.associate { it.categoryId to it.allocatedAmount.toCleanString() }
    }
    fun prepareEdit(b: Budget) {
        _editingCategoryId.value = null
        _subCategoryDrafts.value = emptyList()
        _title.value = b.title
        _manualAmountOverride.value = if (b.amount > 0) b.amount.toCleanString() else null
        _period.value = b.period
        _startDate.value = b.startDate
        _endDate.value = b.endDate ?: "Select a date"
        _budgetType.value = b.budgetType
        _categories.value = b.allocations.map { it.category ?: Category(id = it.categoryId, title = "Unknown", iconResId = "category", colorHex = "0xFF9E9E9E") }
        _manualAmounts.value = b.allocations.filter { it.allocatedAmount > 0 }.associate { it.categoryId to it.allocatedAmount.toCleanString() }
    }

    private fun Double.toCleanString(): String = if (this % 1 == 0.0) this.toLong().toString() else this.toString()
}
