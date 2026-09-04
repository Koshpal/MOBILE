package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.data.UserPreferences
import kotlinx.coroutines.flow.map

class ProfileFluxDeck(
    userPreferences: UserPreferences
) {
    val username = userPreferences.username
    val email = userPreferences.email
    val phone = userPreferences.phone

    val firstName = username.map { name -> name.split(" ").firstOrNull() ?: "" }

    val isBiometricEnabled = userPreferences.isBiometricEnabled
    val incomingTransactionsNotif = userPreferences.incomingTransactionsNotif
    val budgetAlertsNotif = userPreferences.budgetAlertsNotif
    val duesRemindersNotif = userPreferences.duesRemindersNotif
    val goalsProgressNotif = userPreferences.goalsProgressNotif

    fun clear() {
        // ProfileFluxDeck primarily reflects UserPreferences which are cleared separately.
    }
}