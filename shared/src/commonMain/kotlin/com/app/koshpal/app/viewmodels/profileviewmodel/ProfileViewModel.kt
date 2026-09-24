package com.app.koshpal.app.viewmodels.profileviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.coordinator.ProfileCoordinator
import com.app.koshpal.app.fluxdeck.ProfileFluxDeck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val coordinator: ProfileCoordinator,
    private val fluxDeck: ProfileFluxDeck
) : ViewModel() {

    private val _activeSheet = MutableStateFlow("")
    val activeSheet: StateFlow<String> = _activeSheet.asStateFlow()

    val isBottomSheetActive = _activeSheet.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val fullName = fluxDeck.username
    val firstName = fluxDeck.firstName
    val phone = fluxDeck.phone
    val email = fluxDeck.email

    val isBiometricEnabled = fluxDeck.isBiometricEnabled
    val isAutoSiriTransactionsEnabled = fluxDeck.isAutoSiriTransactionsEnabled
    val isAutoMessageTransactionsEnabled = fluxDeck.isAutoMessageTransactionsEnabled
    val incomingTransactionsNotif = fluxDeck.incomingTransactionsNotif
    val budgetAlertsNotif = fluxDeck.budgetAlertsNotif
    val duesRemindersNotif = fluxDeck.duesRemindersNotif
    val goalsProgressNotif = fluxDeck.goalsProgressNotif

    fun updateActiveSheet(value: String) {
        _activeSheet.value = value
    }

    fun toggleBiometric(enabled: Boolean) = coordinator.updateBiometric(enabled)
    fun toggleAutoSiriTransactions(enabled: Boolean) = coordinator.updateAutoSiriTransactions(enabled)
    fun toggleAutoMessageTransactions(enabled: Boolean) = coordinator.updateAutoMessageTransactions(enabled)
    fun toggleIncomingTransactionsNotif(enabled: Boolean) = coordinator.updateIncomingTransactionsNotif(enabled)
    fun toggleBudgetAlertsNotif(enabled: Boolean) = coordinator.updateBudgetAlertsNotif(enabled)
    fun toggleDuesRemindersNotif(enabled: Boolean) = coordinator.updateDuesRemindersNotif(enabled)
    fun toggleGoalsProgressNotif(enabled: Boolean) = coordinator.updateGoalsProgressNotif(enabled)

    fun logout() = coordinator.logout()

    fun resetState() {
        _activeSheet.value = ""
    }
}
