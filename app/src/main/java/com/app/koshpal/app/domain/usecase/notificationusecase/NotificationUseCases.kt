package com.app.koshpal.app.domain.usecase.notificationusecase

import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.repository.NotificationRepo
import kotlinx.coroutines.flow.Flow

class GetAllNotificationsUseCase(private val repo: NotificationRepo) {
    operator fun invoke(): Flow<List<Notification>> = repo.getAllNotifications()
}

class InsertNotificationUseCase(private val repo: NotificationRepo) {
    suspend operator fun invoke(notification: Notification) = repo.insertNotification(notification)
}

class DeleteOldNotificationsUseCase(private val repo: NotificationRepo) {
    suspend operator fun invoke(threshold: Long) = repo.deleteOldNotifications(threshold)
}

class MarkNotificationAsReadUseCase(private val repo: NotificationRepo) {
    suspend operator fun invoke(id: String) = repo.markAsRead(id)
}

class GetNotificationsInRangeUseCase(private val repo: NotificationRepo) {
    operator fun invoke(startTime: Long, endTime: Long): Flow<List<Notification>> =
        repo.getNotificationsInRange(startTime, endTime)
}

class ClearAllNotificationsUseCase(private val repo: NotificationRepo) {
    suspend operator fun invoke() = repo.clearAll()
}

data class NotificationUseCases(
    val getAllNotifications: GetAllNotificationsUseCase,
    val insertNotification: InsertNotificationUseCase,
    val deleteOldNotifications: DeleteOldNotificationsUseCase,
    val markNotificationAsRead: MarkNotificationAsReadUseCase,
    val getNotificationsInRange: GetNotificationsInRangeUseCase,
    val clearAllNotifications: ClearAllNotificationsUseCase
)
