package com.app.koshpal.app.domain.usecase.dueusecase

import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.core.alarm.ReminderScheduler
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

private val MONTH_NAMES = arrayOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

class ScheduleReminderUseCase(private val scheduler: ReminderScheduler) {
    operator fun invoke(due: Due) {
        scheduler.schedule(due)
    }

    fun calculateNextOccurrence(due: Due): Long? {
        val nowMillis = Clock.System.now().toEpochMilliseconds()
        val reminderTime = due.reminderTime ?: nowMillis
        val tz = TimeZone.currentSystemDefault()
        
        var nextInstant = Instant.fromEpochMilliseconds(reminderTime)

        fun advanceInstant(current: Instant): Instant {
            val localDateTime = current.toLocalDateTime(tz)
            val date = localDateTime.date
            val time = localDateTime.time
            val nextDate = when (due.frequency) {
                "Weekly" -> date.plus(7, DateTimeUnit.DAY)
                "Monthly" -> date.plus(1, DateTimeUnit.MONTH)
                "Quarterly" -> date.plus(3, DateTimeUnit.MONTH)
                "Every 6 months" -> date.plus(6, DateTimeUnit.MONTH)
                "Every Year" -> date.plus(1, DateTimeUnit.YEAR)
                "Custom" -> date.plus(due.customFrequencyDays ?: 1, DateTimeUnit.DAY)
                else -> date
            }
            return Instant.parse("${nextDate}T${time}")
        }

        if (due.frequency == "One-time" || due.frequency.isBlank()) return null

        nextInstant = advanceInstant(nextInstant)
        while (nextInstant.toEpochMilliseconds() < nowMillis) {
            nextInstant = advanceInstant(nextInstant)
        }

        return nextInstant.toEpochMilliseconds()
    }

    fun formatDisplayDate(timestamp: Long): String {
        val date = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val day = date.dayOfMonth
        val month = MONTH_NAMES[date.monthNumber - 1]
        val year = date.year
        return "$day $month $year"
    }
}
