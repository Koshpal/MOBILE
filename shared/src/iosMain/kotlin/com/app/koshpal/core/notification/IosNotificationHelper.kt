package com.app.koshpal.core.notification

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IosNotificationHelper : NotificationHelper {

    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override fun showReminderNotification(id: String, title: String, message: String) {
        showNotification(id, title, message)
    }

    override fun showClassifyNotification(transactionId: String, amount: Double, partyName: String) {
        val title = if (partyName != "Unknown") "Paid ₹$amount to $partyName" else "New Transaction: ₹$amount"
        showNotification(transactionId, title, "Tap to classify this transaction.")
    }

    override fun showSyncNotification() {
        // No persistent background sync banner needed on iOS
    }

    override fun cancelSyncNotification() {
        // No-op on iOS
    }

    override fun showBudgetNotification(id: String, title: String, message: String) {
        showNotification(id, title, message)
    }

    override fun showGoalNotification(id: String, title: String, message: String) {
        showNotification(id, title, message)
    }

    override fun cancelNotification(id: Int) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id.toString()))
        center.removeDeliveredNotificationsWithIdentifiers(listOf(id.toString()))
    }

    private fun showNotification(id: String, title: String, message: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound())
        }
        val request = UNNotificationRequest.requestWithIdentifier(id, content, null)
        center.addNotificationRequest(request, null)
    }
}
