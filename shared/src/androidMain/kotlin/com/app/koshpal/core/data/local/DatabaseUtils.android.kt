package com.app.koshpal.core.data.local

actual suspend fun AppDatabase.clearAllTablesKmp() {
    clearAllTables()
}
