package com.app.koshpal.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.app.koshpal.MainActivity
import com.app.koshpal.R
import com.app.koshpal.core.alarm.ReminderReceiver

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val REMINDER_CHANNEL_ID = "reminder_alarm_channel"
        const val REMINDER_CHANNEL_NAME = "Dues & Reminders (Alarms)"
        const val CLASSIFY_CHANNEL_ID = "classify_channel"
        const val CLASSIFY_CHANNEL_NAME = "Transactions"
        const val BUDGET_GOAL_CHANNEL_ID = "budget_goal_channel"
        const val BUDGET_GOAL_CHANNEL_NAME = "Budgets & Goals"
        const val SYNC_CHANNEL_ID = "sync_channel"
        const val SYNC_CHANNEL_NAME = "Background Sync"
        const val SYNC_NOTIFICATION_ID = 9999
        
        const val ACTION_MARK_PAID = "com.app.koshpal.ACTION_MARK_PAID"
        const val ACTION_DISMISS = "com.app.koshpal.ACTION_DISMISS"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                REMINDER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alarms for dues and reminders"
                enableVibration(true)
                setBypassDnd(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setSound(
                    android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI,
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            
            val classifyChannel = NotificationChannel(
                CLASSIFY_CHANNEL_ID,
                CLASSIFY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for classifying new transactions"
            }

            val budgetGoalChannel = NotificationChannel(
                BUDGET_GOAL_CHANNEL_ID,
                BUDGET_GOAL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for budget warnings and goal milestones"
            }

            val syncChannel = NotificationChannel(
                SYNC_CHANNEL_ID,
                SYNC_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Status notifications for background synchronization"
            }
            
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(classifyChannel)
            notificationManager.createNotificationChannel(budgetGoalChannel)
            notificationManager.createNotificationChannel(syncChannel)
        }
    }

    fun showReminderNotification(id: String, title: String, message: String) {
        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(context, id.hashCode(), fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        val markPaidIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_MARK_PAID
            putExtra("DUE_ID", id)
        }
        val markPaidPendingIntent = PendingIntent.getBroadcast(context, id.hashCode() + 1, markPaidIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val dismissIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra("DUE_ID", id)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(context, id.hashCode() + 2, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications_24px)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(true)
            .setSound(android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)
            .addAction(R.drawable.check_circle_24px, "Mark as Paid", markPaidPendingIntent)
            .addAction(R.drawable.close_24px, "Dismiss", dismissPendingIntent)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }

    fun showClassifyNotification(transactionId: String, amount: Double, partyName: String) {
        val title = if (partyName != "Unknown") "Paid ₹$amount to $partyName" else "New Transaction: ₹$amount"
        val message = "Tap to classify this transaction and update your budget."
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("transaction_id", transactionId)
            putExtra("navigate_to", "classify_transaction")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 
            transactionId.hashCode(), 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CLASSIFY_CHANNEL_ID)
            .setSmallIcon(R.drawable.category_24px)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(transactionId.hashCode(), notification)
    }

    fun showSyncNotification() {
        val notification = NotificationCompat.Builder(context, SYNC_CHANNEL_ID)
            .setSmallIcon(R.drawable.swap_horiz_24px)
            .setContentTitle("Koshpal Sync")
            .setContentText("Syncing data in background...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        notificationManager.notify(SYNC_NOTIFICATION_ID, notification)
    }

    fun cancelSyncNotification() {
        notificationManager.cancel(SYNC_NOTIFICATION_ID)
    }

    fun showBudgetNotification(id: String, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, BUDGET_GOAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.pie_chart_24px)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }

    fun showGoalNotification(id: String, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, BUDGET_GOAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.target_24px)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }
}
