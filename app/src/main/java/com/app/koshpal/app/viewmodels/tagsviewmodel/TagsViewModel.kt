package com.app.koshpal.app.viewmodels.tagsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.fluxdeck.TagsFluxDeck
import com.app.koshpal.app.domain.coordinator.TagsCoordinator
import com.app.koshpal.app.domain.model.Transactions
import kotlinx.coroutines.flow.*

class TagsViewModel(
    private val coordinator: TagsCoordinator,
    private val fluxDeck: TagsFluxDeck
) : ViewModel() {

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _isIndividualEditing = MutableStateFlow("")
    val isIndividualEditing: StateFlow<String> = _isIndividualEditing.asStateFlow()

    private val _selectedTagIds = MutableStateFlow<List<String>>(emptyList())
    val selectedItem: StateFlow<List<String>> = _selectedTagIds.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll: StateFlow<Boolean> = _selectAll.asStateFlow()

    private val _isFilterVisible = MutableStateFlow(false)
    val isFilterVisible: StateFlow<Boolean> = _isFilterVisible.asStateFlow()

    private val _activeSheet = MutableStateFlow("")
    val activeSheet: StateFlow<String> = _activeSheet.asStateFlow()

    val isBottomSheetActive = combine(_isEditing, _isFilterVisible, _activeSheet) { editing, filter, sheet ->
        editing || filter || sheet.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _clickedGoalId = MutableStateFlow("")
    val clickedGoalId: StateFlow<String> = _clickedGoalId.asStateFlow()

    val events = coordinator.events

    val searchQuery = fluxDeck.searchQuery
    val selectedPeriod = fluxDeck.selectedPeriod
    val showHidden = fluxDeck.showHidden
    
    val allTags = fluxDeck.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets = fluxDeck.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isAnySelectedHidden: StateFlow<Boolean> = combine(_selectedTagIds, fluxDeck.hiddenTagIds) { selected, hidden ->
        selected.any { hidden.contains(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val filteredTags = fluxDeck.filteredTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val detailAnalytics = fluxDeck.detailAnalytics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateSearchQuery(value: String) = fluxDeck.updateSearchQuery(value)
    fun updateSelectedPeriod(value: String) = fluxDeck.updateSelectedPeriod(value)
    fun toggleShowHidden() = fluxDeck.toggleShowHidden()

    fun updateIsEditing(editing: Boolean) {
        _isEditing.value = editing
        if (editing) {
            _isFilterVisible.value = false
            _activeSheet.value = ""
            clearSelection()
        }
    }

    fun updateIsIndividualEditing(id: String) { _isIndividualEditing.value = id }

    fun updateIsFilterVisible(visible: Boolean) {
        _isFilterVisible.value = visible
        if (visible) {
            _isEditing.value = false
            _activeSheet.value = ""
        }
    }

    fun updateActiveSheet(value: String) { 
        _activeSheet.value = value 
        if (value.isNotEmpty()) {
            _isEditing.value = false
            _isFilterVisible.value = false
        }
    }

    fun addSelectedItem(id: String) { if (!_selectedTagIds.value.contains(id)) _selectedTagIds.update { it + id } }
    fun removeSelectedItem(id: String) { _selectedTagIds.update { it - id } }

    fun updateSelectAll(value: Boolean) {
        _selectAll.value = value
        if (value) _selectedTagIds.value = filteredTags.value.map { it.tag.id } else clearSelection()
    }

    fun clearSelection() {
        _selectedTagIds.value = emptyList()
        _selectAll.value = false
    }

    fun toggleSelectionHiddenState() {
        coordinator.toggleSelectionHiddenState(_selectedTagIds.value)
        resetEditingState()
    }

    fun excludeSelection() {
        coordinator.excludeSelection(_selectedTagIds.value)
        _isEditing.value = false
        clearSelection()
    }

    fun resetEditingState() {
        _isEditing.value = false
        _isFilterVisible.value = false
        _activeSheet.value = ""
        _selectedTagIds.value = emptyList()
        _selectAll.value = false
    }

    fun updateClickedTagId(id: String) {
        fluxDeck.updateClickedTagId(id)
    }

    fun updateClickedGoalId(id: String) {
        _clickedGoalId.value = id
        coordinator.updateClickedGoalId(id)
    }

    fun deleteGoal(goal: Goal) {
        coordinator.deleteGoal(goal)
    }

    fun getSpentAmountForCategory(categoryId: String): Flow<Double> {
        return fluxDeck.getSpentAmountForCategory(categoryId)
    }

    fun getSpentAmountForSubCategory(subCategory: Category): Flow<Double> {
        return fluxDeck.getSpentAmountForCategory(subCategory.id)
    }

    fun getTransactionsForCategory(categoryId: String): Flow<Transactions> {
        return fluxDeck.getTransactionsForCategory(categoryId)
    }

    fun getSubCategoriesForCategory(categoryId: String): Flow<List<Category>> {
        return coordinator.getSubCategoriesForCategory(categoryId)
    }

    fun deleteTag(tagId: String) {
        allTags.value.find { it.id == tagId }?.let {
            coordinator.deleteTag(it)
        }
    }

    fun excludeIndividualCategory(id: String) { }
    fun addSelectedCategory(id: String) { }
    fun removeSelectedCategory(id: String) { }
}
