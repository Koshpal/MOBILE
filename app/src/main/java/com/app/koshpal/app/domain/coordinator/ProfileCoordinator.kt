package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.fluxdeck.ProfileFluxDeck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProfileCoordinator(
    private val fluxDeck: ProfileFluxDeck,
    private val userPreferences: UserPreferences,
    private val authCoordinator: AuthCoordinator,
    private val scope: CoroutineScope
) {
    fun logout() {
        authCoordinator.logout()
    }

    fun updateBiometric(enabled: Boolean) {
        scope.launch { userPreferences.updateBiometric(enabled) }
    }

    fun updateIncomingTransactionsNotif(enabled: Boolean) {
        scope.launch { userPreferences.updateIncomingTransactionsNotif(enabled) }
    }

    fun updateBudgetAlertsNotif(enabled: Boolean) {
        scope.launch { userPreferences.updateBudgetAlertsNotif(enabled) }
    }

    fun updateDuesRemindersNotif(enabled: Boolean) {
        scope.launch { userPreferences.updateDuesRemindersNotif(enabled) }
    }

    fun updateGoalsProgressNotif(enabled: Boolean) {
        scope.launch { userPreferences.updateGoalsProgressNotif(enabled) }
    }
}