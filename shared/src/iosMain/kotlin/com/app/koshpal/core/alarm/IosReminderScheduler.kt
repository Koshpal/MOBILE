package com.app.koshpal.core.alarm

import com.app.koshpal.app.domain.model.Due
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.time.Clock

class IosReminderScheduler : ReminderScheduler {

    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override fun canScheduleExactAlarms(): Boolean = true

    override fun requestExactAlarmPermission() {
        center.requestAuthorizationWithOptions(
            options = (1uL or 2uL or 4uL) // alert, sound, badge
        ) { _, _ -> }
    }

    override fun schedule(due: Due) {
        val reminderTime = due.reminderTime ?: return
        val timeIntervalSeconds = (reminderTime - Clock.System.now().toEpochMilliseconds()) / 1000.0
        if (timeIntervalSeconds <= 0) return

        val content = UNMutableNotificationContent().apply {
            setTitle("Reminder: ${due.title}")
            setBody("Due: ${due.title} - ₹${due.amount}")
            setSound(UNNotificationSound.defaultSound())
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(timeIntervalSeconds, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(due.id, content, trigger)
        center.addNotificationRequest(request, null)
    }

    override fun cancel(dueId: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(dueId))
        center.removeDeliveredNotificationsWithIdentifiers(listOf(dueId))
    }
}
