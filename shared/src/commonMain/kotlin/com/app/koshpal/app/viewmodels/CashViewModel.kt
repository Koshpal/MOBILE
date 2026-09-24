package com.app.koshpal.app.viewmodels

import com.app.koshpal.app.domain.model.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.fluxdeck.CashFluxDeck
import com.app.koshpal.app.domain.coordinator.CashCoordinator
import kotlinx.coroutines.flow.*

class CashViewModel(
    private val coordinator: CashCoordinator,
    private val fluxDeck: CashFluxDeck
) : ViewModel() {

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll: StateFlow<Boolean> = _selectAll.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    private val _isFilterVisible = MutableStateFlow(false)
    val isFilterVisible: StateFlow<Boolean> = _isFilterVisible.asStateFlow()

    val events = coordinator.events

    val searchQuery = fluxDeck.searchQuery
    val filterPeriod = fluxDeck.filterPeriod
    val startDate = fluxDeck.startDate
    val endDate = fluxDeck.endDate

    val allTransactions = fluxDeck.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Transactions(emptyList()))

    val cashBalance = fluxDeck.cashBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cashTrend = fluxDeck.cashTrend
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendDateRange = fluxDeck.trendDateRange
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "No data" to "No data")

    val filteredTransactions = fluxDeck.filteredTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTagName(id: String?): String? = fluxDeck.getTagName(id)
    fun getCategoryName(budgetId: String?, categoryId: String?): String? = fluxDeck.getCategoryName(budgetId, categoryId)

    fun onSearchQueryChange(query: String) = fluxDeck.updateSearchQuery(query)
    fun onFilterPeriodChange(value: String) = fluxDeck.updateFilterPeriod(value)

    fun updateIsFilterVisible(visible: Boolean) {
        _isFilterVisible.value = visible
        if (visible) _isEditing.value = false
    }

    fun updateIsEditing(editing: Boolean) {
        _isEditing.value = editing
        if (editing) {
            _isFilterVisible.value = false
            clearSelection()
        } else {
            clearSelection()
        }
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
        val selectedTransactions = allTransactions.value.transactions.filter { _selectedIds.value.contains(it.id) }
        if (selectedTransactions.isNotEmpty()) {
            coordinator.deleteTransactions(Transactions(selectedTransactions))
            updateIsEditing(false)
        }
    }
}
