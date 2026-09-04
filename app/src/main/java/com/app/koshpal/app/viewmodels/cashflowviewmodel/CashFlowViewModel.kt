package com.app.koshpal.app.viewmodels.cashflowviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.fluxdeck.CashFlowFluxDeck
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

class CashFlowViewModel(
    private val fluxDeck: CashFlowFluxDeck,
) : ViewModel() {

    val searchQuery = fluxDeck.searchQuery
    val selectedMonth = fluxDeck.selectedMonth

    val incomeThisMonth = fluxDeck.incomeThisMonth
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val expenseThisMonth = fluxDeck.expenseThisMonth
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val leftThisMonth = fluxDeck.leftThisMonth
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val investedThisMonth = fluxDeck.investedThisMonth
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val incomingTransactions = fluxDeck.incomingTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outgoingTransactions = fluxDeck.outgoingTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dualLineTrendData = fluxDeck.dualLineTrendData
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) = fluxDeck.updateSearchQuery(query)
    fun onSelectedMonthChange(ym: YearMonth?) = fluxDeck.updateSelectedMonth(ym)
    fun selectPreviousMonth() = fluxDeck.selectPreviousMonth()
    fun selectNextMonth() = fluxDeck.selectNextMonth()
    fun toggleAllTime(showAll: Boolean) = fluxDeck.toggleAllTime(showAll)
}
