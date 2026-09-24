package com.app.koshpal.shared.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.data.createIosDataStore
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.app.domain.usecase.transactionsusecase.IosSmsTransactionSyncer
import com.app.koshpal.app.domain.usecase.transactionsusecase.ProcessIncomingSmsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.SmsTransactionSyncer
import com.app.koshpal.app.domain.usecase.transactionsusecase.SyncSmsTransactionsUseCase
import com.app.koshpal.app.viewmodels.CashViewModel
import com.app.koshpal.app.viewmodels.HomeViewModel
import com.app.koshpal.app.viewmodels.authviewmodel.AuthViewModel
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetCreationViewModel
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetSettingsViewModel
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetViewModel
import com.app.koshpal.app.viewmodels.cashflowviewmodel.CashFlowViewModel
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesCreationViewModel
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesViewModel
import com.app.koshpal.app.viewmodels.goalsviewmodel.GoalCreationViewModel
import com.app.koshpal.app.viewmodels.goalsviewmodel.GoalViewModel
import com.app.koshpal.app.viewmodels.notificationsviewmodel.NotificationsViewModel
import com.app.koshpal.app.viewmodels.profileviewmodel.ProfileViewModel
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsCreationViewModel
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsViewModel
import com.app.koshpal.app.viewmodels.transactionsviewmodel.DetailedTransactionViewModel
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionCreationViewModel
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionsViewModel
import com.app.koshpal.core.alarm.IosReminderScheduler
import com.app.koshpal.core.alarm.ReminderScheduler
import com.app.koshpal.core.data.local.AppDatabase
import com.app.koshpal.core.data.local.AppDatabaseConstructor
import com.app.koshpal.core.notification.IosNotificationHelper
import com.app.koshpal.core.notification.NotificationHelper
import com.app.koshpal.core.util.logError
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

private var _koin: Koin? = null

val MIGRATION_28_29_IOS = object : Migration(28, 29) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `budgets` ADD COLUMN `isRepeating` INTEGER NOT NULL DEFAULT 0")
    }
}

val iosModule = module {
    single<CoroutineDispatcher>(named("IODispatcher")) { Dispatchers.Default }
    single { createIosDataStore() }
    single {
        Room.databaseBuilder<AppDatabase>(
            name = NSHomeDirectory() + "/Documents/koshpal_database.db",
            factory = { AppDatabaseConstructor.initialize() }
        )
        .addMigrations(MIGRATION_28_29_IOS)
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .build()
    }
    single { Darwin.create() }
    single<NotificationHelper> { IosNotificationHelper() }
    single<ReminderScheduler> { IosReminderScheduler() }
    single<SmsTransactionSyncer> { IosSmsTransactionSyncer() }
}

fun doInitKoin() {
    try {
        val koinApp = startKoin {
            modules(iosModule, sharedModule)
        }
        _koin = koinApp.koin
    } catch (e: Throwable) {
        logError("doInitKoin failed", e)
    }
}

fun getBudgetCreationViewModel(): BudgetCreationViewModel = _koin!!.get()
fun getBudgetViewModel(): BudgetViewModel = _koin!!.get()
fun getBudgetSettingsViewModel(): BudgetSettingsViewModel = _koin!!.get()
fun getHomeViewModel(): HomeViewModel = _koin!!.get()
fun getGoalViewModel(): GoalViewModel = _koin!!.get()
fun getGoalCreationViewModel(): GoalCreationViewModel = _koin!!.get()
fun getDuesViewModel(): DuesViewModel = _koin!!.get()
fun getDuesCreationViewModel(): DuesCreationViewModel = _koin!!.get()
fun getCashFlowViewModel(): CashFlowViewModel = _koin!!.get()
fun getCashViewModel(): CashViewModel = _koin!!.get()
fun getTransactionCreationViewModel(): TransactionCreationViewModel = _koin!!.get()
fun getAuthViewModel(): AuthViewModel = _koin!!.get()
fun getUserPreferences(): UserPreferences = _koin!!.get()
fun getProfileViewModel(): ProfileViewModel = _koin!!.get()
fun getNotificationsViewModel(): NotificationsViewModel = _koin!!.get()
fun getTagsViewModel(): TagsViewModel = _koin!!.get()
fun getTagsCreationViewModel(): TagsCreationViewModel = _koin!!.get()
fun getTransactionsRepo(): TransactionsRepo = _koin!!.get()
fun getBudgetRepo(): BudgetRepo = _koin!!.get()
fun getTransactionsViewModel(): TransactionsViewModel = _koin!!.get()
fun getDetailedTransactionViewModel(): DetailedTransactionViewModel = _koin!!.get()
fun getProcessIncomingSmsUseCase(): ProcessIncomingSmsUseCase = _koin!!.get()
fun getSyncSmsTransactionsUseCase(): SyncSmsTransactionsUseCase = _koin!!.get()
