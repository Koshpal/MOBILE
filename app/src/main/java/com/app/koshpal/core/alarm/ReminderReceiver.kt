package com.app.koshpal.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.usecase.dueusecase.DueUseCases
import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class ReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationHelper: NotificationHelper by inject()
    private val dueUseCases: DueUseCases by inject()
    private val notificationUseCases: NotificationUseCases by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val dueId = intent.getStringExtra("DUE_ID") ?: return

        val pendingResult = goAsync()

        if (action == NotificationHelper.ACTION_MARK_PAID) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    markDueAsPaid(dueId)
                    notificationHelper.cancelNotification(dueId.hashCode())
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        if (action == NotificationHelper.ACTION_DISMISS) {
            notificationHelper.cancelNotification(dueId.hashCode())
            pendingResult.finish()
            return
        }

        val dueTitle = intent.getStringExtra("DUE_TITLE") ?: "Reminder"
        val message = "Your scheduled reminder is due now!"

        notificationHelper.showReminderNotification(
            id = dueId,
            title = dueTitle,
            message = message
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = dueUseCases.getDueById(dueId)
                if (result is Result.Success) {
                    val due = result.data
                    notificationUseCases.insertNotification(
                        Notification(
                            id = UUID.randomUUID().toString(),
                            type = NotificationType.DUE_REMINDER,
                            title = "Reminder Due",
                            message = "${due?.title ?: dueTitle} is due today.",
                            timestamp = System.currentTimeMillis(),
                            featureId = dueId,
                            iconResId = due?.iconResId
                        )
                    )

                    if (due != null) {
                        val nextTime = dueUseCases.scheduleReminder.calculateNextOccurrence(due)
                        val isRecurring = due.frequency != "One-time" && nextTime != null

                        if (due.isCompleted) {
                            if (isRecurring) {
                                val nextDateStr = dueUseCases.scheduleReminder.formatDisplayDate(nextTime)
                                val updatedDue = due.copy(
                                    reminderTime = nextTime,
                                    date = nextDateStr,
                                    isCompleted = false
                                )
                                dueUseCases.updateDue(updatedDue)
                                dueUseCases.scheduleReminder(updatedDue)
                            }
                        } else {
                            val overdueDue = due.copy(
                                overdueInfo = "Payment pending for ${due.date}",
                                reminderTime = null
                            )
                            dueUseCases.updateDue(overdueDue)

                            if (isRecurring) {
                                val nextDateStr = dueUseCases.scheduleReminder.formatDisplayDate(nextTime)
                                val newUpcoming = due.copy(
                                    id = UUID.randomUUID().toString(),
                                    date = nextDateStr,
                                    reminderTime = nextTime,
                                    isCompleted = false,
                                    overdueInfo = null
                                )
                                dueUseCases.insertDue(newUpcoming)
                                dueUseCases.scheduleReminder(newUpcoming)
                            }
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun markDueAsPaid(dueId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = dueUseCases.getDueById(dueId)
            if (result is Result.Success) {
                val due = result.data
                if (due != null) {
                    val nextTime = dueUseCases.scheduleReminder.calculateNextOccurrence(due)
                    val isRecurring = due.frequency != "One-time" && nextTime != null

                    val completedDue = due.copy(
                        isCompleted = true,
                        overdueInfo = null,
                        reminderTime = null
                    )
                    dueUseCases.updateDue(completedDue)

                    if (isRecurring) {
                        val nextDateStr = dueUseCases.scheduleReminder.formatDisplayDate(nextTime)
                        val newUpcoming = due.copy(
                            id = UUID.randomUUID().toString(),
                            date = nextDateStr,
                            reminderTime = nextTime,
                            isCompleted = false,
                            overdueInfo = null
                        )
                        dueUseCases.insertDue(newUpcoming)
                        dueUseCases.scheduleReminder(newUpcoming)
                    }
                }
            }
        }
    }
}
