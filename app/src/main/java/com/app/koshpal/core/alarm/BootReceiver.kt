package com.app.koshpal.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.koshpal.app.domain.usecase.dueusecase.DueUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val dueUseCases: DueUseCases by inject()
    private val scheduler: ReminderScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val allDues = dueUseCases.getAllDues().first()
                allDues.forEach { due ->
                    if (!due.isCompleted && due.reminderTime != null) {
                        scheduler.schedule(due)
                    }
                }
            }
        }
    }
}
