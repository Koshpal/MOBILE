package com.app.koshpal.app.viewmodels.authviewmodel


import androidx.lifecycle.ViewModel
import com.app.koshpal.app.domain.coordinator.AuthCoordinator
import com.app.koshpal.app.fluxdeck.AuthFluxDeck

class AuthViewModel(
    coordinator: AuthCoordinator,
    private val fluxDeck: AuthFluxDeck
) : ViewModel() {

    val email = fluxDeck.email
    val password = fluxDeck.password
    val isLoading = fluxDeck.isLoading
    val events = coordinator.events

    fun onEmailChange(value: String) = fluxDeck.updateEmail(value)
    fun onPasswordChange(value: String) = fluxDeck.updatePassword(value)

    fun login() {
        fluxDeck.login()
    }

    fun onBoarding() {
        fluxDeck.onBoarding()
    }

    fun clearState() = fluxDeck.clear()
}
