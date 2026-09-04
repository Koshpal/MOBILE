package com.app.koshpal.app.viewmodels.transactionsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.app.domain.coordinator.TransactionsCoordinator
import kotlinx.coroutines.flow.*

class TransactionsViewModel(
    private val coordinator: TransactionsCoordinator,
    private val fluxDeck: TransactionsFluxDeck
) : ViewModel() {

    private val _isFilterVisible = MutableStateFlow(false)
    val isFilterVisible = _isFilterVisible.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    private val _isIndividualEditing = MutableStateFlow("")
    val isIndividualEditing = _isIndividualEditing.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll = _selectAll.asStateFlow()

    val events = coordinator.events

    val searchQuery = fluxDeck.searchQuery
    val selectedTab = fluxDeck.selectedTab
    val syncStatus = fluxDeck.syncStatus

    val typeFilter = fluxDeck.typeFilter
    val showBookmarked = fluxDeck.showBookmarked
    val showCash = fluxDeck.showCash
    val showWithNotes = fluxDeck.showWithNotes
    val showWithReceipts = fluxDeck.showWithReceipts
    val showWithoutPayorPayee = fluxDeck.showWithoutPayorPayee
    val showExcludedFromCashFlow = fluxDeck.showExcludedFromCashFlow
    val startDate = fluxDeck.startDate
    val endDate = fluxDeck.endDate

    val availableDateBounds = fluxDeck.availableDateRange
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(System.currentTimeMillis(), System.currentTimeMillis()))

    val filteredTransactions = fluxDeck.filteredTransactions
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val groupedTransactions = fluxDeck.groupedTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


    fun getTagName(id: String?): String? = fluxDeck.getTagName(id)
    fun getCategoryName(budgetId: String?, categoryId: String?): String? = fluxDeck.getCategoryName(budgetId, categoryId)

    fun onSearchQueryChange(query: String) = fluxDeck.updateSearchQuery(query)
    fun onTabSelect(tab: String) = fluxDeck.updateSelectedTab(tab)

    fun updateClickedTransactionId(id: String) = fluxDeck.updateTransactionId(id)

    fun syncSmsTransactions() {
        coordinator.syncSmsTransactions()
    }

    fun updateIsEditing(editing: Boolean) {
        _isEditing.value = editing
        if (!editing) {
            clearSelection()
            updateIsIndividualEditing("")
        }
    }

    fun updateIsIndividualEditing(id: String) {
        _isIndividualEditing.value = id
    }

    fun deleteTransaction(id: String) {
        coordinator.deleteTransactions(listOf(id))
        updateIsIndividualEditing("")
    }

    fun addSelectedItem(id: String) {
        _selectedIds.update { it + id }
    }

    fun removeSelectedItem(id: String) {
        _selectedIds.update { it - id }
    }

    fun updateSelectAll(value: Boolean) {
        _selectAll.value = value
        if (value) {
            _selectedIds.value = filteredTransactions.value.map { it.id }.toSet()
        } else {
            clearSelection()
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
        _selectAll.value = false
    }

    fun deleteSelection() {
        val selectedIdsList = _selectedIds.value.toList()
        if (selectedIdsList.isNotEmpty()) {
            coordinator.deleteTransactions(selectedIdsList)
            updateIsEditing(false)
        }
    }

    fun updateIsFilterVisible(visible: Boolean) {
        _isFilterVisible.value = visible
        if (visible) updateIsEditing(false)
    }

    fun updateTypeFilter(value: String) = fluxDeck.updateTypeFilter(value)
    fun updateShowBookmarked(value: Boolean) = fluxDeck.updateShowBookmarked(value)
    fun updateShowCash(value: Boolean) = fluxDeck.updateShowCash(value)
    fun updateShowWithNotes(value: Boolean) = fluxDeck.updateShowWithNotes(value)
    fun updateShowWithReceipts(value: Boolean) = fluxDeck.updateShowWithReceipts(value)
    fun updateShowWithoutPayorPayee(value: Boolean) = fluxDeck.updateShowWithoutPayorPayee(value)
    fun updateShowExcludedFromCashFlow(value: Boolean) = fluxDeck.updateShowExcludedFromCashFlow(value)
    fun updateDateRange(start: Long?, end: Long?) = fluxDeck.updateDateRange(start, end)
}
