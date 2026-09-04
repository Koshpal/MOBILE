package com.app.koshpal.app.viewmodels.transactionsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.app.domain.coordinator.TransactionsCoordinator
import kotlinx.coroutines.flow.*

class DetailedTransactionViewModel(
    private val coordinator: TransactionsCoordinator,
    private val fluxDeck: TransactionsFluxDeck
) : ViewModel() {

    val transaction = fluxDeck.transaction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val partyActivityDates = fluxDeck.partyActivityDates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPartyDate = fluxDeck.selectedPartyDate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val filteredDetailedTransactions = fluxDeck.detailedFilteredTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val detailedHeaderAmount = fluxDeck.detailedHeaderAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val partyInsight = fluxDeck.partyInsight
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getBudgetName(id: String?): String? = fluxDeck.getBudgetName(id)
    fun getTagName(id: String?): String? = fluxDeck.getTagName(id)
    fun getCategoryName(budgetId: String?, categoryId: String?): String? = fluxDeck.getCategoryName(budgetId, categoryId)

    val events = coordinator.events

    fun onDateSelected(date: Long?) {
        fluxDeck.updateSelectedPartyDate(date)
    }
}
