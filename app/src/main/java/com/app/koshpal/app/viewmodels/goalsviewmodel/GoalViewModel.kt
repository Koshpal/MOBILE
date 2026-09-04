package com.app.koshpal.app.viewmodels.goalsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.domain.coordinator.GoalCoordinator
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class GoalViewModel(
    private val coordinator: GoalCoordinator,
    private val fluxDeck: GoalFluxDeck
) : ViewModel() {

    private val _isEditingList = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditingList.asStateFlow()

    private val _isFilterVisible = MutableStateFlow(false)
    val isFilterVisible: StateFlow<Boolean> = _isFilterVisible.asStateFlow()

    val isBottomSheetActive = combine(_isEditingList, _isFilterVisible) { editing, filter ->
        editing || filter
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isIndividualEditing = MutableStateFlow("")
    val isIndividualEditing: StateFlow<String> = _isIndividualEditing.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedItem: StateFlow<Set<String>> = _selectedItemIds.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll: StateFlow<Boolean> = _selectAll.asStateFlow()

    val events = coordinator.events

    val searchQuery = fluxDeck.searchQuery
    val showHistory = fluxDeck.showHistory
    val filterPeriod = fluxDeck.filterPeriod
    val filterDate = fluxDeck.filterDate

    val goals: StateFlow<List<Goal>> = fluxDeck.filteredGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGoal: StateFlow<Goal?> = fluxDeck.activeGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeGoalTag: StateFlow<Tag?> = fluxDeck.activeGoalTag
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    fun getTimeRemaining(goal: Goal) = fluxDeck.getTimeRemaining(goal)
    fun getRecommendedPerDay(goal: Goal) = fluxDeck.getRecommendedPerDay(goal)

    fun updateIsEditing(editing: Boolean) {
        _isEditingList.value = editing
        if (editing) _isFilterVisible.value = false
    }

    fun updateIsIndividualEditing(id: String) { _isIndividualEditing.value = id }
    fun updateClickedGoalId(id: String) = fluxDeck.updateClickedGoalId(id)

    fun updateIsFilterVisible(visible: Boolean) {
        _isFilterVisible.value = visible
        if (visible) _isEditingList.value = false
    }

    fun toggleHistory() = fluxDeck.toggleHistory()

    fun updateFilterPeriod(period: BudgetPeriod?) = fluxDeck.updateFilterPeriod(period)
    fun updateFilterDate(date: LocalDate?) = fluxDeck.updateFilterDate(date)

    fun addSelectedItem(id: String) { _selectedItemIds.update { it + id } }
    fun removeSelectedItem(id: String) { _selectedItemIds.update { it - id } }
    fun clearSelectedItem() {
        _selectedItemIds.value = emptySet()
        _selectAll.value = false
    }

    fun updateSelectAll(value: Boolean) {
        _selectAll.value = value
        if (value) _selectedItemIds.value = goals.value.map { it.id }.toSet() else clearSelectedItem()
    }

    val totalSavedOfSelected: StateFlow<Double> = combine(_selectedItemIds, fluxDeck.allGoals) { selected, all ->
        all.filter { selected.contains(it.id) }.sumOf { it.savedAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun deleteSelectedGoals() {
        coordinator.deleteSelectedGoals(_selectedItemIds.value.toList())
        resetEditingState()
    }

    fun resetEditingState() {
        _isEditingList.value = false; _isFilterVisible.value = false
        _selectedItemIds.value = emptySet(); _selectAll.value = false
    }

    val totalAmountSaved: StateFlow<Double> = fluxDeck.totalAmountSaved
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val achievementPercentage: StateFlow<Int> = fluxDeck.achievementPercentage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val historyStats: StateFlow<GoalFluxDeck.HistoryStats> = fluxDeck.historyStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalFluxDeck.HistoryStats(0, 0.0, 0.0))

    fun onSearchQueryChange(query: String) = fluxDeck.updateSearchQuery(query)

    fun addFunds(goal: Goal, amount: Double) = coordinator.addFunds(goal, amount)
    fun removeFunds(goal: Goal, amount: Double) = coordinator.removeFunds(goal, amount)
    fun deleteGoal(goal: Goal) = coordinator.deleteGoal(goal)

    fun prepareEditGoal(goal: Goal) {
        fluxDeck.prepareEdit(goal)
    }

    val allTags: StateFlow<List<Tag>> = fluxDeck.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
