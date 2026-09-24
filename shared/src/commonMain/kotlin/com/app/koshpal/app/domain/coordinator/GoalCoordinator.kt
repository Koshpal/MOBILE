package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.usecase.authusecase.AuthUseCases
import com.app.koshpal.app.domain.usecase.goalusecase.GoalUseCases
import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import com.app.koshpal.app.domain.usecase.tagusecase.TagUseCases
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.handleResult
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.notification.NotificationHelper
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import com.app.koshpal.core.presentation.util.toIso8601String
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GoalCoordinator(
    private val goalUseCases: GoalUseCases,
    private val tagUseCases: TagUseCases,
    private val notificationUseCases: NotificationUseCases,
    private val notificationHelper: NotificationHelper,
    private val userPreferences: UserPreferences,
    private val fluxDeck: GoalFluxDeck,
    private val authUseCases: AuthUseCases,
    private val scope: CoroutineScope
) {
    val reflector = StateReflector<Events>(scope)
    val events = reflector.events

    init {
        sync()
        scope.launch {
            fluxDeck.saveGoalIntent.collect {
                saveGoalFromDraft()
            }
        }
    }

    fun syncRemoteGoals() {
        scope.launch {
            if (userPreferences.isGuestUser.first()) return@launch
            val token = userPreferences.accessToken.first() ?: return@launch
            val result = goalUseCases.getRemoteGoalsUseCase(token)
            reflector.handleResult(
                result,
                onError = { error ->
                    if (error == NetworkError.INVALID_USER) {
                        onRefreshToken {
                            syncRemoteGoals()
                        }
                    }
                }
            ) { remoteGoals ->
                remoteGoals.forEach { goal ->
                    goalUseCases.createGoal(goal)
                }
            }
        }
    }

    fun onRefreshToken(onSuccess: () -> Unit = {}) {
        scope.launch {
            if (userPreferences.isGuestUser.first()) return@launch
            val currentRefreshToken = userPreferences.refreshToken.first() ?: return@launch
            val currentRefreshTokenId = userPreferences.refreshTokenId.first() ?: ""
            val result = authUseCases.onRefreshTokenUseCase(currentRefreshToken, currentRefreshTokenId)
            reflector.handleResult(result) { user ->
                userPreferences.saveAccessToken(user.accessToken)
                userPreferences.saveRefreshToken(user.refreshToken)
                if (!user.refreshTokenId.isNullOrBlank()) {
                    userPreferences.saveRefreshTokenId(user.refreshTokenId)
                }
                onSuccess()
            }
        }
    }

    private fun sync() {
        scope.launch {
            syncRemoteGoals()
        }
        scope.launch {
            goalUseCases.getAllGoals().collectLatest { fluxDeck.updateAllGoals(it) }
        }
        scope.launch {
            tagUseCases.getAllTags().collectLatest { fluxDeck.updateAllTags(it) }
        }
        scope.launch {
            userPreferences.hiddenGoalIds.collectLatest { fluxDeck.updateHiddenGoalIds(it) }
        }
        scope.launch {
            userPreferences.flaggedGoalIds.collectLatest { fluxDeck.updateFlaggedGoalIds(it) }
        }

        scope.launch {
            fluxDeck.allGoals
                .collectLatest { list ->
                    list.forEach { goal ->
                        val percent = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount) * 100 else 0.0
                        when {
                            percent >= 90 -> triggerGoalNotification(goal, "90%")
                            percent >= 50 -> triggerGoalNotification(goal, "50%")
                        }
                    }
                }
        }
    }

    private suspend fun triggerGoalNotification(goal: Goal, threshold: String) {
        val existing = notificationUseCases.getAllNotifications().first()
        val alreadyNotified = existing.any {
            it.type == NotificationType.GOAL_INSIGHT &&
                    it.featureId == goal.id &&
                    it.message.contains(threshold)
        }

        if (!alreadyNotified) {
            val title = "Goal Milestone: ${goal.title}"
            val message = "You have reached $threshold of your goal!"

            notificationUseCases.insertNotification(
                Notification(
                    id = Uuid.random().toString(),
                    title = title,
                    message = message,
                    type = NotificationType.GOAL_INSIGHT,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    featureId = goal.id
                )
            )

            notificationHelper.showGoalNotification(
                id = goal.id,
                title = title,
                message = message
            )
        }
    }

    fun deleteGoal(goal: Goal) {
        scope.launch {
            val result = goalUseCases.deleteGoal(goal)
            reflector.handleResult(result)
            if (!userPreferences.isGuestUser.first()) {
                val token = userPreferences.accessToken.first()
                if (token != null) {
                    val remoteResult = goalUseCases.deleteRemoteGoalUseCase(token, goal.id)
                    reflector.handleResult(
                        remoteResult,
                        onError = { error ->
                            if (error == NetworkError.INVALID_USER) {
                                onRefreshToken {
                                    deleteGoal(goal)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    fun deleteSelectedGoals(selectedIds: List<String>) {
        scope.launch {
            goalUseCases.deleteGoalsByIds(selectedIds)
            reflector.emitEvent(Events.Success("${selectedIds.size} goals deleted"))
        }
    }

    fun addFunds(goal: Goal, amount: Double) {
        scope.launch {
            val updated = goal.copy(savedAmount = goal.savedAmount + amount)
            val result = goalUseCases.updateGoal(updated)
            reflector.handleResult(result)
        }
    }

    fun removeFunds(goal: Goal, amount: Double) {
        scope.launch {
            val updated = goal.copy(savedAmount = (goal.savedAmount - amount).coerceAtLeast(0.0))
            val result = goalUseCases.updateGoal(updated)
            reflector.handleResult(result)
        }
    }

    suspend fun saveGoalFromDraft() {
        val targetAmountValue = fluxDeck.draftTargetAmount.value.toDoubleOrNull() ?: 0.0
        val editingGoalId = fluxDeck.editingGoalId.value
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val goalToSave = if (editingGoalId != null) {
            val existingGoal = fluxDeck.allGoals.value.find { it.id == editingGoalId } ?: return

            val durationMonths = if (fluxDeck.isDateEnabled.value) {
                val creationDate = existingGoal.creationDate.parseIsoToLocalDate() ?: today
                val targetLocalDate = Instant.fromEpochMilliseconds(fluxDeck.draftDate.value)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val months = (targetLocalDate.year - creationDate.year) * 12 + (targetLocalDate.monthNumber - creationDate.monthNumber)
                months.coerceAtLeast(1)
            } else existingGoal.durationMonths

            existingGoal.copy(
                title = fluxDeck.draftTitle.value,
                targetAmount = targetAmountValue,
                monthlySavings = if (durationMonths > 0) targetAmountValue / durationMonths else targetAmountValue,
                durationMonths = durationMonths,
                iconResId = fluxDeck.draftIcon.value,
                colorHex = fluxDeck.draftColor.value,
                tagId = fluxDeck.draftTagId.value,
                imageUri = fluxDeck.draftImageUri.value
            )
        } else {
            val durationMonths = if (fluxDeck.isDateEnabled.value) {
                val targetLocalDate = Instant.fromEpochMilliseconds(fluxDeck.draftDate.value)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val months = (targetLocalDate.year - today.year) * 12 + (targetLocalDate.monthNumber - today.monthNumber)
                months.coerceAtLeast(1)
            } else 12

            Goal(
                id = Uuid.random().toString(),
                title = fluxDeck.draftTitle.value,
                targetAmount = targetAmountValue,
                savedAmount = 0.0,
                monthlySavings = if (durationMonths > 0) targetAmountValue / durationMonths else targetAmountValue,
                durationMonths = durationMonths,
                iconResId = fluxDeck.draftIcon.value,
                colorHex = fluxDeck.draftColor.value,
                creationDate = Clock.System.now().toEpochMilliseconds().toIso8601String(),
                isAchieved = false,
                tagId = fluxDeck.draftTagId.value,
                imageUri = fluxDeck.draftImageUri.value
            )
        }

        val result = if (editingGoalId != null) {
            fluxDeck.updateLoading(true)
            val res = goalUseCases.updateGoal(goalToSave)
            fluxDeck.updateLoading(false)
            res
        } else {
            fluxDeck.updateLoading(true)
            val res = goalUseCases.createGoal(goalToSave)
            fluxDeck.updateLoading(false)
            res
        }

        reflector.handleResult(result) {
            fluxDeck.clearGoalDraft()
            if (editingGoalId != null) {
                reflector.emitEvent(Events.Success("Goal updated"))
            } else {
                reflector.emitEvent(Events.Success("Goal created"))
            }
        }
    }
}
