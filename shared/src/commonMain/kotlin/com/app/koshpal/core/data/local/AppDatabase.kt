package com.app.koshpal.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.app.koshpal.core.data.entities.*
import com.app.koshpal.core.data.local.dao.*

@TypeConverters(Converters::class)
@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        BudgetAllocationEntity::class,
        BudgetHistoryEntity::class,
        DueEntity::class,
        ReminderTypeEntity::class,
        TagEntity::class,
        GoalEntity::class,
        NotificationEntity::class
    ],
    version = 29,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun dueDao(): DueDao
    abstract fun reminderTypeDao(): ReminderTypeDao
    abstract fun tagDao(): TagDao
    abstract fun goalDao(): GoalDao
    abstract fun notificationDao(): NotificationDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
