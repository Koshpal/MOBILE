package com.app.koshpal.app.viewmodels.profileviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.fluxdeck.ProfileFluxDeck
import com.app.koshpal.app.domain.coordinator.ProfileCoordinator
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.*

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
    val incomingTransactionsNotif = fluxDeck.incomingTransactionsNotif
    val budgetAlertsNotif = fluxDeck.budgetAlertsNotif
    val duesRemindersNotif = fluxDeck.duesRemindersNotif
    val goalsProgressNotif = fluxDeck.goalsProgressNotif

    fun updateActiveSheet(value: String) {
        _activeSheet.value = value
    }

    fun toggleBiometric(enabled: Boolean) = coordinator.updateBiometric(enabled)
    fun toggleIncomingTransactionsNotif(enabled: Boolean) = coordinator.updateIncomingTransactionsNotif(enabled)
    fun toggleBudgetAlertsNotif(enabled: Boolean) = coordinator.updateBudgetAlertsNotif(enabled)
    fun toggleDuesRemindersNotif(enabled: Boolean) = coordinator.updateDuesRemindersNotif(enabled)
    fun toggleGoalsProgressNotif(enabled: Boolean) = coordinator.updateGoalsProgressNotif(enabled)

    fun logout() = coordinator.logout()

    fun openSupport(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@koshpal.com")
            putExtra(Intent.EXTRA_SUBJECT, "Support Request - Koshpal")
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openFeedback(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://koshpal.com/feedback"))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openTermsOfServices(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://koshpal.com/terms"))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openPrivacyPolicy(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://koshpal.com/privacy"))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openDataCompliance(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://koshpal.com/data-compliance"))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
    
    fun resetState() {
        _activeSheet.value = ""
    }
}
