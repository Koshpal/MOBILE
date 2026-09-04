package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class NotificationsFluxDeck(
    private val notificationUseCases: NotificationUseCases
) {
    private val _selectedDate = MutableStateFlow(
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val selectedDate = _selectedDate.asStateFlow()

    val allNotifications = notificationUseCases.getAllNotifications()

    val last7Days = flow {
        val today = LocalDate.now()
        val days = (0..6).map { 
            today.minusDays(it.toLong())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.reversed()
        emit(days)
    }

    val notificationsForSelectedDate = combine(allNotifications, _selectedDate) { notifications, selected ->
        val selectedLocalDate = Instant.ofEpochMilli(selected).atZone(ZoneId.systemDefault()).toLocalDate()
        notifications.filter { 
            val txnLocalDate = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
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
        _selectedDate.value = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    suspend fun markAsRead(id: String) {
        notificationUseCases.markNotificationAsRead(id)
    }

    suspend fun clearHistory() {
        notificationUseCases.clearAllNotifications()
    }
    
    suspend fun deleteOldNotifications() {
        val threshold = LocalDate.now().minusDays(7)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        notificationUseCases.deleteOldNotifications(threshold)
    }
}
