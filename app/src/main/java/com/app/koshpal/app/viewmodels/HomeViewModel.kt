package com.app.koshpal.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.fluxdeck.HomeFluxDeck
import com.app.koshpal.app.domain.coordinator.DuesCoordinator
import kotlinx.coroutines.flow.*

class HomeViewModel(
    private val fluxDeck: HomeFluxDeck,
    private val budgetFluxDeck: BudgetFluxDeck,
    private val duesCoordinator: DuesCoordinator,
    userPreferences: UserPreferences,
) : ViewModel() {
// ...
    fun toggleDueCompletion(metadata: DueWithMetadata) {
        duesCoordinator.toggleDueCompletion(metadata.due)
    }

    val firstName = userPreferences.username
        .map { name -> name.split(" ").firstOrNull() ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val monthlyBudgetContext = fluxDeck.monthlyBudgetContext
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetContext())

    val activeMonthlyBudget = fluxDeck.activeMonthlyBudget
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions = fluxDeck.monthTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Transactions(emptyList()))

    val spendingSummary = fluxDeck.spendingSummary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpendingSummary())

    val untaggedAmount = fluxDeck.untaggedAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val topDues = fluxDeck.topDues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val recentTransactions = fluxDeck.recentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Transactions(emptyList()))

    val tagsSummary = fluxDeck.tagsSummary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<Goal>> = fluxDeck.goals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTagName(id: String?): String? = fluxDeck.getTagName(id)
    fun getCategoryName(budgetId: String?, categoryId: String?): String? = fluxDeck.getCategoryName(budgetId, categoryId)

    fun onMonthSummaryClick(onNavigate: () -> Unit) {
        val context = monthlyBudgetContext.value
        if (context.count == 1 && context.firstId != null) {
            budgetFluxDeck.updateClickedBudgetId(context.firstId)
            budgetFluxDeck.updateIsItemClicked(true)
        } else {
            budgetFluxDeck.updateIsItemClicked(false)
            budgetFluxDeck.updateSearchQuery(context.monthName)
        }
        onNavigate()
    }

}
