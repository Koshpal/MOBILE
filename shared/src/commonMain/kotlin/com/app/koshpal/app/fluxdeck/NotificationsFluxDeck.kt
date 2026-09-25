package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import kotlinx.coroutines.flow.*
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class NotificationsFluxDeck(
    private val notificationUseCases: NotificationUseCases
) {
    private val tz = TimeZone.currentSystemDefault()

    private val _selectedDate = MutableStateFlow(
        Clock.System.now().toEpochMilliseconds()
    )
    val selectedDate = _selectedDate.asStateFlow()

    val allNotifications = notificationUseCases.getAllNotifications()

    val last7Days = flow {
        val today = Clock.System.now().toLocalDateTime(tz).date
        val days = (0..6).map {
            val date = today.minus(it, DateTimeUnit.DAY)
            Instant.parse("${date}T00:00:00Z").toEpochMilliseconds()
        }.reversed()
        emit(days)
    }

    val notificationsForSelectedDate = combine(allNotifications, _selectedDate) { notifications, selected ->
        val selectedLocalDate = Instant.fromEpochMilliseconds(selected).toLocalDateTime(tz).date
        notifications.filter { 
            val txnLocalDate = Instant.fromEpochMilliseconds(it.timestamp).toLocalDateTime(tz).date
            txnLocalDate == selectedLocalDate
        }
    }

    val groupedNotifications = notificationsForSelectedDate.map { list ->
        list.groupBy { it.type }
    }

    fun updateSelectedDate(timestamp: Long) {
        _selectedDate.value = timestamp
    }

    fun clear() {
        _selectedDate.value = Clock.System.now().toEpochMilliseconds()
    }

    suspend fun markAsRead(id: String) {
        notificationUseCases.markNotificationAsRead(id)
    }

    suspend fun clearHistory() {
        notificationUseCases.clearAllNotifications()
    }
    
    suspend fun deleteOldNotifications() {
        val today = Clock.System.now().toLocalDateTime(tz).date
        val thresholdDate = today.minus(7, DateTimeUnit.DAY)
        val threshold = Instant.parse("${thresholdDate}T00:00:00Z").toEpochMilliseconds()
        notificationUseCases.deleteOldNotifications(threshold)
    }
}
