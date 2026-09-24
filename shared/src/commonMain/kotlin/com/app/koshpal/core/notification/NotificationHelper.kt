package com.app.koshpal.core.notification

interface NotificationHelper {
    fun showReminderNotification(id: String, title: String, message: String)
    fun showClassifyNotification(transactionId: String, amount: Double, partyName: String)
    fun showSyncNotification()
    fun cancelSyncNotification()
    fun showBudgetNotification(id: String, title: String, message: String)
    fun showGoalNotification(id: String, title: String, message: String)
    fun cancelNotification(id: Int)

    companion object {
        const val ACTION_MARK_PAID = "com.app.koshpal.ACTION_MARK_PAID"
        const val ACTION_DISMISS = "com.app.koshpal.ACTION_DISMISS"
    }
}
