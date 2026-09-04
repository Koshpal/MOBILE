package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.app.domain.usecase.dueusecase.DueUseCases
import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import com.app.koshpal.app.domain.usecase.reminderType.ReminderTypeUseCases
import com.app.koshpal.app.fluxdeck.DuesFluxDeck
import com.app.koshpal.app.handleResult
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.core.notification.NotificationHelper
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class DuesCoordinator(
    private val dueUseCases: DueUseCases,
    private val reminderTypeUseCases: ReminderTypeUseCases,
    private val notificationUseCases: NotificationUseCases,
    private val notificationHelper: NotificationHelper,
    private val userPreferences: UserPreferences,
    private val fluxDeck: DuesFluxDeck,
    private val scope: CoroutineScope
) {
    val reflector = StateReflector<Events>(scope)
    val events = reflector.events

    private val dateFormatter = DateTimeFormatter.ofPattern("[dd MMM yyyy][dd MMMM yyyy][dd-MM-yyyy]", Locale.ENGLISH)

    init {
        sync()
        scope.launch {
            fluxDeck.saveIntent.collect {
                saveDue()
            }
        }
    }

    private fun sync() {
        scope.launch {
            dueUseCases.getAllDues().collectLatest {
                fluxDeck.updateAllDues(it)
                checkOverdueDues(it)
            }
        }
        scope.launch {
            reminderTypeUseCases.getAllReminderTypes().collectLatest { fluxDeck.updateReminderTypes(it) }
        }
        scope.launch {
            userPreferences.hiddenDueIds.collectLatest { fluxDeck.updateExcludedDueIds(it) }
        }
    }

    private fun checkOverdueDues(dues: List<Due>) {
        val today = LocalDate.now()
        val overdueItems = dues.filter { due ->
            !due.isCompleted && try {
                LocalDate.parse(due.date, dateFormatter).isBefore(today)
            } catch (_: Exception) { false }
        }

        overdueItems.forEach { due ->
            scope.launch {
                val existing = notificationUseCases.getAllNotifications().first()
                val todayStr = today.toString()

                val alreadyNotifiedToday = existing.any {
                    it.featureId == due.id &&
                    it.type == NotificationType.DUE_REMINDER &&
                    Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate().toString() == todayStr
                }

                if (!alreadyNotifiedToday) {
                    val message = "${due.title} is overdue since ${due.date}."

                    notificationUseCases.insertNotification(
                        Notification(
                            id = UUID.randomUUID().toString(),
                            type = NotificationType.DUE_REMINDER,
                            title = "Overdue Reminder",
                            message = message,
                            timestamp = System.currentTimeMillis(),
                            featureId = due.id,
                            iconResId = due.iconResId
                        )
                    )

                    notificationHelper.showReminderNotification(
                        id = due.id,
                        title = "Overdue: ${due.title}",
                        message = message
                    )
                }
            }
        }
    }

    fun deleteDue(due: Due) {
        scope.launch {
            val result = dueUseCases.deleteDue(due)
            reflector.handleResult(result) {
                reflector.emitEvent(Events.Success("Reminder deleted"))
            }
        }
    }

    fun deleteDues(ids: List<String>) {
        scope.launch {
            val result = dueUseCases.deleteDuesByIds(ids)
            reflector.handleResult(result) {
                reflector.emitEvent(Events.Success("${ids.size} reminders deleted"))
            }
        }
    }

    fun toggleHiddenDue(id: String) {
        scope.launch {
            val hidden = userPreferences.hiddenDueIds.first()
            userPreferences.updateHiddenDues(if (hidden.contains(id)) hidden - id else hidden + id)
            reflector.emitEvent(Events.Success("Visibility updated"))
        }
    }

    fun toggleDueCompletion(due: Due) {
        scope.launch {
            val updatedDue = due.copy(isCompleted = !due.isCompleted)
            val result = dueUseCases.updateDue(updatedDue)
            reflector.handleResult(result) {
                if (!updatedDue.isCompleted) {
                    dueUseCases.scheduleReminder(updatedDue)
                }
                val msg = if (updatedDue.isCompleted) "Reminder completed" else "Reminder pending"
                reflector.emitEvent(Events.Success(msg))
            }
        }
    }

    fun insertReminderType(reminderType: ReminderType) {
        scope.launch {
            val result = reminderTypeUseCases.insertReminderType(reminderType)
            reflector.handleResult(result)
        }
    }


    suspend fun saveDue() {
        val reminderTimeMillis = calculateTimestamp(
            fluxDeck.reminderDate.value,
            fluxDeck.reminderHour.value,
            fluxDeck.reminderMinute.value
        )

        val due = Due(
            id = UUID.randomUUID().toString(),
            title = fluxDeck.reminderTitle.value,
            amount = fluxDeck.reminderAmount.value.toDoubleOrNull() ?: 0.0,
            date = fluxDeck.reminderDate.value,
            status = "Pending",
            frequency = fluxDeck.reminderFrequency.value,
            type = fluxDeck.transactionType.value.name,
            reminderType = fluxDeck.selectedReminderType.value?.name,
            iconResId = fluxDeck.selectedReminderType.value?.iconResId,
            colorHex = fluxDeck.selectedReminderType.value?.colorHex,
            reminderTime = reminderTimeMillis,
            customFrequencyDays = fluxDeck.customFrequencyDays.value
        )

        fluxDeck.updateLoading(true)
        val result = dueUseCases.insertDue(due)
        fluxDeck.updateLoading(false)
        reflector.handleResult(result) {
            dueUseCases.scheduleReminder(due)
            fluxDeck.clearReminderForm()
            reflector.emitEvent(Events.Success("Due added"))
        }
    }

    private fun calculateTimestamp(dateStr: String, hour: Int, minute: Int): Long? {
        val localDate = dateStr.parseIsoToLocalDate() ?: return null
        return localDate.atTime(hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

}
