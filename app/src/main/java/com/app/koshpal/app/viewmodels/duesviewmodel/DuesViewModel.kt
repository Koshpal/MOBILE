package com.app.koshpal.app.viewmodels.duesviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.fluxdeck.DuesFluxDeck
import com.app.koshpal.app.domain.coordinator.DuesCoordinator
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class DuesViewModel(
    private val coordinator: DuesCoordinator,
    private val fluxDeck: DuesFluxDeck
) : ViewModel() {

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _isFilterVisible = MutableStateFlow(false)
    val isFilterVisible: StateFlow<Boolean> = _isFilterVisible.asStateFlow()

    val isBottomSheetActive = combine(_isEditing, _isFilterVisible) { editing, filter ->
        editing || filter
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isIndividualEditing = MutableStateFlow("")
    val isIndividualEditing: StateFlow<String> = _isIndividualEditing.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedItem: StateFlow<Set<String>> = _selectedItemIds.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll: StateFlow<Boolean> = _selectAll.asStateFlow()

    val events = coordinator.events

    val clickedDueId = fluxDeck.clickedDueId
    val searchQuery = fluxDeck.searchQuery
    val searchSuggestions = fluxDeck.searchSuggestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val selectedTab = fluxDeck.selectedTab
    val showCompletedReminders = fluxDeck.showCompletedReminders
    val filterDate = fluxDeck.filterDate

    val dues = fluxDeck.filteredDues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredDues = fluxDeck.filteredDues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUpcomingAmount = fluxDeck.totalUpcomingAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalOverdueAmount = fluxDeck.totalOverdueAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun updateSearchQuery(value: String) = fluxDeck.updateSearchQuery(value)
    fun updateSelectedTab(value: String) = fluxDeck.updateSelectedTab(value)
    fun updateClickedDueId(value: String) = fluxDeck.updateClickedDueId(value)

    fun updateIsEditing(editing: Boolean) {
        _isEditing.value = editing
        if (editing) {
            _isFilterVisible.value = false
            clearSelectedItem()
        }
    }

    fun updateIsIndividualEditing(id: String) { _isIndividualEditing.value = id }
    fun toggleShowCompletedReminders() = fluxDeck.toggleShowCompletedReminders()

    fun addSelectedItem(id: String) { _selectedItemIds.update { it + id } }
    fun removeSelectedItem(id: String) { _selectedItemIds.update { it - id } }

    fun updateSelectAll(value: Boolean) {
        _selectAll.value = value
        if (value) addAllSelectedItem() else clearSelectedItem()
    }

    fun updateIsFilterVisible(visible: Boolean) {
        _isFilterVisible.value = visible
        if (visible) {
            _isEditing.value = false
        }
    }

    fun updateFilterDate(date: LocalDate?) = fluxDeck.updateFilterDate(date)
    fun clearReminderForm() = fluxDeck.clearReminderForm()

    fun deleteDue(id: String) {
        val target = fluxDeck.allDues.value.find { it.id == id } ?: dues.value.find { it.id == id }
        if (target != null) {
            coordinator.deleteDue(target)
        }
    }

    fun deleteDue(due: Due) {
        coordinator.deleteDue(due)
    }

    fun addAllSelectedItem() {
        _selectedItemIds.value = filteredDues.value.map { it.id }.toSet()
    }

    fun clearSelectedItem() {
        _selectedItemIds.value = emptySet()
        _selectAll.value = false
    }

    fun excludeSelection() {
        coordinator.deleteDues(_selectedItemIds.value.toList())
        updateIsEditing(false)
    }

    fun resetEditingState() {
        _isEditing.value = false; _isFilterVisible.value = false
        _selectedItemIds.value = emptySet(); _selectAll.value = false
    }

    fun toggleDueCompletion(id: String) {
        val target = fluxDeck.allDues.value.find { it.id == id }
        if (target != null) {
            coordinator.toggleDueCompletion(target)
        }
    }

    fun insertReminderType(reminderType: ReminderType) {
        coordinator.insertReminderType(reminderType)
    }
}
