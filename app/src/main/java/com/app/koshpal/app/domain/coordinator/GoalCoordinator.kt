package com.app.koshpal.app.domain.coordinator


import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.usecase.goalusecase.GoalUseCases
import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import com.app.koshpal.app.domain.usecase.tagusecase.TagUseCases
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.handleResult
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.core.notification.NotificationHelper
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import com.app.koshpal.core.presentation.util.toIso8601String
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

class GoalCoordinator(
    private val goalUseCases: GoalUseCases,
    private val tagUseCases: TagUseCases,
    private val notificationUseCases: NotificationUseCases,
    private val notificationHelper: NotificationHelper,
    private val userPreferences: UserPreferences,
    private val fluxDeck: GoalFluxDeck,
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

    private fun sync() {
        scope.launch {
            val result = goalUseCases.syncGoalsUseCase()
            reflector.handleResult(result)
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
                    id = UUID.randomUUID().toString(),
                    title = title,
                    message = message,
                    type = NotificationType.GOAL_INSIGHT,
                    timestamp = System.currentTimeMillis(),
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
        
        val goalToSave = if (editingGoalId != null) {
            val existingGoal = fluxDeck.allGoals.value.find { it.id == editingGoalId } ?: return
            
            val durationMonths = if (fluxDeck.isDateEnabled.value) {
                val creationDate = existingGoal.creationDate.parseIsoToLocalDate() ?: LocalDate.now()
                val targetLocalDate = Instant.ofEpochMilli(fluxDeck.draftDate.value).atZone(ZoneId.systemDefault()).toLocalDate()
                ChronoUnit.MONTHS.between(creationDate, targetLocalDate).toInt().coerceAtLeast(1)
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
            val creationDate = LocalDate.now()
            val durationMonths = if (fluxDeck.isDateEnabled.value) {
                val targetLocalDate = Instant.ofEpochMilli(fluxDeck.draftDate.value).atZone(ZoneId.systemDefault()).toLocalDate()
                ChronoUnit.MONTHS.between(creationDate, targetLocalDate).toInt().coerceAtLeast(1)
            } else 12

            Goal(
                id = UUID.randomUUID().toString(),
                title = fluxDeck.draftTitle.value,
                targetAmount = targetAmountValue,
                savedAmount = 0.0,
                monthlySavings = if (durationMonths > 0) targetAmountValue / durationMonths else targetAmountValue,
                durationMonths = durationMonths,
                iconResId = fluxDeck.draftIcon.value,
                colorHex = fluxDeck.draftColor.value,
                creationDate = System.currentTimeMillis().toIso8601String(),
                isAchieved = false,
                tagId = fluxDeck.draftTagId.value,
                imageUri = fluxDeck.draftImageUri.value
            )
        }
        
        val result = if (editingGoalId != null) {
            fluxDeck.updateLoading(true)
            val r = goalUseCases.updateGoal(goalToSave)
            fluxDeck.updateLoading(false)
            r
        } else {
            fluxDeck.updateLoading(true)
            val r = goalUseCases.createGoal(goalToSave)
            fluxDeck.updateLoading(false)
            r
        }

        reflector.handleResult(result) {
            fluxDeck.clearGoalDraft()
            if(editingGoalId != null) {
                reflector.emitEvent(Events.Success("Goal updated"))
            }else{
                reflector.emitEvent(Events.Success("Goal created"))
            }
        }
    }
}
