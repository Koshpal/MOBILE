package com.app.koshpal.app.viewmodels.budgetviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.domain.coordinator.BudgetCoordinator
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class BudgetViewModel(
    private val coordinator: BudgetCoordinator,
    private val fluxDeck: BudgetFluxDeck
) : ViewModel() {

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _isFilterVisible = MutableStateFlow(false)
    val isFilterVisible: StateFlow<Boolean> = _isFilterVisible.asStateFlow()

    val isBottomSheetActive = combine(_isEditing, _isFilterVisible, fluxDeck.isCreatingCategoryInSheet) { editing, filter, createCat ->
        editing || filter || createCat
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isIndividualEditing = MutableStateFlow("")
    val isIndividualEditing: StateFlow<String> = _isIndividualEditing.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedItem: StateFlow<Set<String>> = _selectedItemIds.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll: StateFlow<Boolean> = _selectAll.asStateFlow()

    private val _selectedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories: StateFlow<Set<String>> = _selectedCategoryIds.asStateFlow()

    val events = coordinator.events

    val searchQuery = fluxDeck.searchQuery
    val searchSuggestions = fluxDeck.searchSuggestions
    val budgetTypeIs = fluxDeck.budgetTypeFilter
    val showHistory = fluxDeck.showHistory
    val filterPeriod = fluxDeck.filterPeriod
    val filterDate = fluxDeck.filterDate
    val showHidden = fluxDeck.showHidden
    val excludedCategoryIds = fluxDeck.excludedCategoryIds
    
    val hiddenCategoryIds: StateFlow<Set<String>> = fluxDeck.hiddenCategoryIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
        
    val hiddenBudgetIds: StateFlow<Set<String>> = fluxDeck.hiddenBudgetIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
        
    val flaggedBudgetIds: StateFlow<Set<String>> = fluxDeck.flaggedBudgetIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val flaggedBudgets: StateFlow<List<Budget>> = fluxDeck.flaggedBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isItemClicked: StateFlow<Boolean> = fluxDeck.isItemClicked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val clickedBudgetId: StateFlow<String> = fluxDeck.clickedBudgetId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val budgets: StateFlow<List<Budget>> = fluxDeck.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyBudgets: StateFlow<List<Budget>> = fluxDeck.archivedBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredBudgets = fluxDeck.filteredBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredHistoryBudgets = fluxDeck.filteredHistoryBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBudgetedAmount = fluxDeck.totalBudgetedAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val isAnySelectedFlagged: StateFlow<Boolean> = fluxDeck.isAnySelectedFlagged(_selectedItemIds, fluxDeck.flaggedBudgetIds)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isAnySelectedHidden: StateFlow<Boolean> = fluxDeck.isAnySelectedHidden(
        isItemClicked = isItemClicked,
        selectedItems = _selectedItemIds,
        selectedCategories = _selectedCategoryIds,
        hiddenBudgets = fluxDeck.hiddenBudgetIds,
        hiddenCategories = fluxDeck.hiddenCategoryIds
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        fluxDeck.hiddenBudgetIds.onEach { 
            if (it.isEmpty() && showHidden.value) fluxDeck.setShowHidden(false)
        }.launchIn(viewModelScope)
    }

    fun updateSearchQuery(q: String) = fluxDeck.updateSearchQuery(q)
    fun updateBudgetTypeIs(t: String) = fluxDeck.updateBudgetTypeFilter(t)
    fun updateIsEditing(e: Boolean) { 
        _isEditing.value = e 
        if (e) _isFilterVisible.value = false else resetEditingState()
    }
    fun updateIsIndividualEditing(id: String) { _isIndividualEditing.value = id }
    fun updateIsItemClicked(c: Boolean) = fluxDeck.updateIsItemClicked(c)
    fun updateClickedBudgetId(id: String) = fluxDeck.updateClickedBudgetId(id)
    fun addSelectedItem(id: String) { _selectedItemIds.update { it + id } }
    fun removeSelectedItem(id: String) { _selectedItemIds.update { it - id } }
    fun clearSelectedItem() { 
        _selectedItemIds.value = emptySet()
        _selectedCategoryIds.value = emptySet()
        _selectAll.value = false 
    }
    fun updateSelectAll(v: Boolean) { _selectAll.value = v; if (v) addAllSelectedItem() else clearSelectedItem() }
    fun addSelectedCategory(id: String) { _selectedCategoryIds.update { it + id } }
    fun removeSelectedCategory(id: String) { _selectedCategoryIds.update { it - id } }

    fun toggleHistory() = fluxDeck.toggleHistory()
    fun updateFilterPeriod(p: BudgetPeriod?) = fluxDeck.updateFilterPeriod(p)
    fun updateFilterDate(d: LocalDate?) = fluxDeck.updateFilterDate(d)
    fun toggleShowHidden() = fluxDeck.toggleShowHidden()
    fun updateIsFilterVisible(v: Boolean) { _isFilterVisible.value = v; if (v) _isEditing.value = false }

    fun addAllSelectedItem() {
        if (isItemClicked.value) {
            val allBudgets = budgets.value + historyBudgets.value
            allBudgets.find { it.id == clickedBudgetId.value }?.let { b ->
                _selectedCategoryIds.value = b.allocations.filter { it.category?.parentCategoryId == null }.map { it.categoryId }.toSet()
            }
        } else {
            val budgetsToSelect = if (showHistory.value) filteredHistoryBudgets.value else filteredBudgets.value
            _selectedItemIds.value = budgetsToSelect.map { it.id }.toSet()
        }
    }

    fun toggleIndividualFlaggedState(id: String) {
        coordinator.toggleIndividualFlaggedState(id, budgets.value + historyBudgets.value)
        updateIsIndividualEditing("")
    }

    fun toggleIndividualHiddenState(id: String) {
        coordinator.toggleIndividualHiddenState(id)
        updateIsIndividualEditing("")
    }

    fun deleteBudget(budget: Budget) {
        coordinator.deleteBudget(budget)
    }

    fun toggleSelectionHiddenState() {
        coordinator.toggleSelectionHiddenState(isItemClicked.value, _selectedCategoryIds.value.toList(), _selectedItemIds.value.toList())
        resetEditingState()
    }

    fun toggleFlaggedState() {
        coordinator.toggleFlaggedState(_selectedItemIds.value.toList(), budgets.value + historyBudgets.value)
        resetEditingState()
    }

    fun removeFlaggedBudget(id: String) {
        coordinator.removeFlaggedBudget(id)
    }

    fun excludeSelection() {
        coordinator.excludeSelection(isItemClicked.value, _selectedCategoryIds.value.toList(), _selectedItemIds.value.toList())
        updateIsEditing(false)
    }

    fun excludeIndividualCategory(id: String) {
        coordinator.excludeIndividualCategory(id)
        updateIsIndividualEditing("")
    }

    fun getSpentAmountForBudget(b: Budget) = fluxDeck.getSpentAmountForBudget(b.id)
    fun getSpentAmountForCategory(c: Category) = fluxDeck.getSpentAmountForCategory(c.id)
    
    fun getSpentAmountForSubCategory(s: Category): Flow<Double> {
        return fluxDeck.getSpentAmountForCategory(s.id)
    }

    fun resetDetailedState() {
        updateIsItemClicked(false); updateClickedBudgetId("")
        if (showHistory.value) fluxDeck.toggleHistory()
        fluxDeck.updateBudgetTypeFilter("all")
        clearBudgetDraft()
        resetEditingState()
    }

    fun resetEditingState() {
        _isEditing.value = false; _isFilterVisible.value = false
        fluxDeck.updateIsCreatingCategoryInSheet(false)
        _selectedItemIds.value = emptySet(); _selectedCategoryIds.value = emptySet(); _selectAll.value = false
    }

    fun clearBudgetDraft() = fluxDeck.clearWorkflow()

    fun prepareCloneBudget(id: String) {
        val allBudgets = budgets.value + historyBudgets.value
        allBudgets.find { it.id == id }?.let { fluxDeck.prepareClone(it) }
    }
}
