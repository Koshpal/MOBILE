package com.app.koshpal.core.data.local

import androidx.room.Transactor
import androidx.room.useWriterConnection

actual suspend fun AppDatabase.clearAllTablesKmp() {
    useWriterConnection { transactor: Transactor ->
        val tables = listOf(
            "transactions", "categories", "budgets", "budget_allocations",
            "budget_history", "dues", "reminder_types", "tags", "goals", "notifications"
        )
        for (table in tables) {
            transactor.usePrepared("DELETE FROM $table") { stmt ->
                stmt.step()
            }
        }
    }
}
