package com.app.koshpal.app.domain.usecase.dueusecase

import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.core.alarm.ReminderScheduler
import java.util.Calendar
import java.util.Locale

class ScheduleReminderUseCase(private val scheduler: ReminderScheduler) {
    operator fun invoke(due: Due) {
        scheduler.schedule(due)
    }

    fun calculateNextOccurrence(due: Due): Long? {
        val currentCalendar = Calendar.getInstance()
        val nextCalendar = Calendar.getInstance().apply {
            timeInMillis = due.reminderTime ?: System.currentTimeMillis()
        }

        when (due.frequency) {
            "Weekly" -> nextCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            "Monthly" -> nextCalendar.add(Calendar.MONTH, 1)
            "Quarterly" -> nextCalendar.add(Calendar.MONTH, 3)
            "Every 6 months" -> nextCalendar.add(Calendar.MONTH, 6)
            "Every Year" -> nextCalendar.add(Calendar.YEAR, 1)
            "Custom" -> {
                val days = due.customFrequencyDays ?: 1
                nextCalendar.add(Calendar.DAY_OF_YEAR, days)
            }
            else -> return null
        }

        while (nextCalendar.before(currentCalendar)) {
            when (due.frequency) {
                "Weekly" -> nextCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                "Monthly" -> nextCalendar.add(Calendar.MONTH, 1)
                "Quarterly" -> nextCalendar.add(Calendar.MONTH, 3)
                "Every 6 months" -> nextCalendar.add(Calendar.MONTH, 6)
                "Every Year" -> nextCalendar.add(Calendar.YEAR, 1)
                "Custom" -> nextCalendar.add(Calendar.DAY_OF_YEAR, due.customFrequencyDays ?: 1)
            }
        }

        return nextCalendar.timeInMillis
    }

    fun formatDisplayDate(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
        val year = calendar.get(Calendar.YEAR)
        return "$day $month $year"
    }
}
