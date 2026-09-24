package com.app.koshpal.core.alarm

import com.app.koshpal.app.domain.model.Due

interface ReminderScheduler {
    fun canScheduleExactAlarms(): Boolean
    fun requestExactAlarmPermission()
    fun schedule(due: Due)
    fun cancel(dueId: String)
}
