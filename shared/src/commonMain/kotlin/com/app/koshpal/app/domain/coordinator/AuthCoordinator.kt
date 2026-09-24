package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.Auth
import com.app.koshpal.app.domain.model.User
import com.app.koshpal.app.domain.usecase.authusecase.AuthUseCases
import com.app.koshpal.app.fluxdeck.AuthFluxDeck
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.fluxdeck.CashFluxDeck
import com.app.koshpal.app.fluxdeck.DuesFluxDeck
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.fluxdeck.HomeFluxDeck
import com.app.koshpal.app.fluxdeck.NotificationsFluxDeck
import com.app.koshpal.app.fluxdeck.ProfileFluxDeck
import com.app.koshpal.app.fluxdeck.TagsFluxDeck
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.app.handleResult
import com.app.koshpal.core.data.local.AppDatabase
import com.app.koshpal.core.data.local.clearAllTablesKmp
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class AuthCoordinator(
    private val authUseCases: AuthUseCases,
    private val userPreferences: UserPreferences,
    private val authFluxDeck: AuthFluxDeck,
    private val budgetFluxDeck: BudgetFluxDeck,
    private val duesFluxDeck: DuesFluxDeck,
    private val tagsFluxDeck: TagsFluxDeck,
    private val transactionsFluxDeck: TransactionsFluxDeck,
    private val cashFluxDeck: CashFluxDeck,
    private val goalFluxDeck: GoalFluxDeck,
    private val profileFluxDeck: ProfileFluxDeck,
    private val notificationsFluxDeck: NotificationsFluxDeck,
    private val homeFluxDeck: HomeFluxDeck,
    private val budgetCoordinator: BudgetCoordinator,
    private val goalCoordinator: GoalCoordinator,
    private val transactionsCoordinator: TransactionsCoordinator,
    private val appDatabase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
) {
    val reflector = StateReflector<Events>(scope)
    val events = reflector.events

    init {
        scope.launch {
            authFluxDeck.loginIntent.collect {
                login()
            }
        }
        scope.launch {
            authFluxDeck.onBoardingIntent.collect {
                onBoarding()
            }
        }
    }

    suspend fun login() {
        authFluxDeck.setLoading(true)
        delay(3.seconds)
        val email = authFluxDeck.email.value
        val password = authFluxDeck.password.value

        if (email.isNotBlank() && password.isNotBlank()) {
            if (email == "guestuser@gmail.com" && password == "123test") {
                val guestAuth = Auth(
                    userId = "guest_id",
                    role = "USER",
                    firstName = "Guest",
                    lastName = "User",
                    phone = "0000000000",
                    isActive = true
                )
                val guestUser = User(
                    user = guestAuth,
                    accessToken = "guest_token",
                    refreshToken = "guest_refresh",
                    redirectUrl = ""
                )
                userPreferences.saveAccessToken(guestUser.accessToken)
                userPreferences.saveUserId(guestUser.user.userId)
                userPreferences.saveUsername("${guestUser.user.firstName} ${guestUser.user.lastName}")
                userPreferences.saveUserDetails(
                    email = email,
                    phone = guestUser.user.phone
                )

                authFluxDeck.setAuthResult(Result.Success(guestUser))
                reflector.emitEvent(Events.Success("guest_login"))
                authFluxDeck.clear()
            } else {
                val result = authUseCases.loginUseCase(email, password)
                reflector.handleResult(result) { user ->
                    userPreferences.saveAccessToken(user.accessToken)
                    userPreferences.saveRefreshToken(user.refreshToken)
                    if (!user.refreshTokenId.isNullOrBlank()) {
                        userPreferences.saveRefreshTokenId(user.refreshTokenId)
                    }
                    userPreferences.setHasCompletedOnboarding(user.user.isOnBoardingComplete)
                    userPreferences.saveUserId(user.user.userId)
                    userPreferences.saveUsername("${user.user.firstName} ${user.user.lastName}")
                    userPreferences.saveUserDetails(
                        email = email,
                        phone = user.user.phone,
                    )
                    reflector.emitEvent(Events.Success("user_login"))
                    authFluxDeck.clear()
                    triggerSync()
                }
                authFluxDeck.setAuthResult(result)
            }
            authFluxDeck.setLoading(false)
        } else {
            authFluxDeck.setLoading(false)
            reflector.emitEvent(Events.Error(NetworkError.UNKNOWN, "Email and password cannot be empty"))
        }
    }

    private fun triggerSync() {
        scope.launch {
            budgetCoordinator.syncRemoteBudgets()
            goalCoordinator.syncRemoteGoals()
            transactionsCoordinator.syncRemoteTransactions()
        }
    }

    suspend fun onBoarding() {
        authFluxDeck.setLoading(true)
        val selectedOptions = authFluxDeck.onBoardingSelectedOptions.value
        val result = authUseCases.onBoardingUseCase(selectedOptions = selectedOptions)
        reflector.handleResult(result) {
            userPreferences.setHasCompletedOnboarding(true)
            reflector.emitEvent(Events.Success("Onboarding complete"))
        }
        authFluxDeck.setLoading(false)
    }


    fun logout() {
        scope.launch {
            withContext(ioDispatcher) {
                appDatabase.clearAllTablesKmp()
                userPreferences.clearAuth()
            }

            authFluxDeck.clear()
            budgetFluxDeck.clear()
            duesFluxDeck.clear()
            tagsFluxDeck.clear()
            transactionsFluxDeck.clear()
            cashFluxDeck.clear()
            goalFluxDeck.clear()
            profileFluxDeck.clear()
            notificationsFluxDeck.clear()
            homeFluxDeck.clear()

            reflector.emitEvent(Events.LoggedOut("Logged out successfully"))
        }
    }
}
