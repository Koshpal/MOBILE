package com.app.koshpal.app.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.koshpal.app.data.createAndroidDataStore
import com.app.koshpal.app.domain.usecase.transactionsusecase.ProcessIncomingSmsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.SmsTransactionSyncer
import com.app.koshpal.app.domain.usecase.transactionsusecase.SyncSmsTransactionsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.TransactionUseCases
import com.app.koshpal.core.alarm.AndroidReminderScheduler
import com.app.koshpal.core.alarm.ReminderScheduler
import com.app.koshpal.core.data.local.AppDatabase
import com.app.koshpal.core.notification.AndroidNotificationHelper
import com.app.koshpal.core.notification.NotificationHelper
import com.app.koshpal.core.sms.AndroidSmsTransactionSyncer
import com.app.koshpal.core.sms.SmsTransactionPipeline
import com.app.koshpal.core.sms.dedup.DuplicateDetector
import com.app.koshpal.core.sms.dedup.DuplicateDetectorImpl
import com.app.koshpal.core.sms.filter.SmsFilter
import com.app.koshpal.core.sms.filter.SmsFilterImpl
import com.app.koshpal.core.sms.parser.BankIdentityParser
import com.app.koshpal.core.sms.parser.TransactionSmsParser
import com.app.koshpal.core.sms.parser.bank.BankParser
import com.app.koshpal.core.sms.reader.SmsReader
import com.app.koshpal.core.sms.reader.SmsReaderImpl
import com.app.koshpal.core.sms.util.ContactResolver
import com.app.koshpal.core.sms.validate.TransactionValidator
import com.app.koshpal.core.sms.validate.TransactionValidatorImpl
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `dues` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, `status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `overdueInfo` TEXT, `isCompleted` INTEGER NOT NULL, `iconRes` INTEGER, `iconBackgroundColor` INTEGER, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `reminder_types` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `iconResId` TEXT, `colorHex` TEXT NOT NULL, `lastModifiedTimeStamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE `dues_new` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, `status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `type` TEXT NOT NULL, `reminderType` TEXT, `overdueInfo` TEXT, `isCompleted` INTEGER NOT NULL, `iconResId` TEXT, `colorHex` TEXT, PRIMARY KEY(`id`))")
        db.execSQL("INSERT INTO `dues_new` (id, title, date, amount, status, frequency, type, reminderType, overdueInfo, isCompleted, iconResId, colorHex) SELECT id, title, date, amount, status, frequency, 'EXPENSE', NULL, overdueInfo, isCompleted, CAST(iconRes AS TEXT), CAST(iconBackgroundColor AS TEXT) FROM `dues`")
        db.execSQL("DROP TABLE `dues`")
        db.execSQL("ALTER TABLE `dues_new` RENAME TO `dues`")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `dues` ADD COLUMN `reminderTime` INTEGER")
        db.execSQL("ALTER TABLE `dues` ADD COLUMN `customFrequencyDays` INTEGER")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `tagId` TEXT")
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `budgetGoal` REAL NOT NULL, `colorHex` TEXT NOT NULL, `lastModifiedTimeStamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE `transactions_new` (`id` TEXT NOT NULL, `accountId` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `category` TEXT NOT NULL, `subCategory` TEXT NOT NULL, `source` TEXT NOT NULL, `description` TEXT NOT NULL, `transactionDate` INTEGER NOT NULL, `merchant` TEXT NOT NULL, `bank` TEXT NOT NULL, `maskedAccountNo` INTEGER NOT NULL, `provider` TEXT NOT NULL, `isSynced` INTEGER NOT NULL, `tagId` TEXT, PRIMARY KEY(`id`))")
        db.execSQL("INSERT INTO `transactions_new` (id, accountId, amount, type, category, subCategory, source, description, transactionDate, merchant, bank, maskedAccountNo, provider, isSynced, tagId) SELECT id, accountId, CAST(amount AS REAL), type, category, subCategory, source, description, transactionDate, merchant, bank, maskedAccountNo, provider, isSynced, tagId FROM `transactions`")
        db.execSQL("DROP TABLE `transactions`")
        db.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE `transactions_new` (`id` TEXT NOT NULL, `accountId` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `category` TEXT NOT NULL, `subCategory` TEXT NOT NULL, `source` TEXT NOT NULL, `description` TEXT NOT NULL, `transactionDate` INTEGER NOT NULL, `merchant` TEXT NOT NULL, `bank` TEXT NOT NULL, `maskedAccountNo` INTEGER NOT NULL, `provider` TEXT NOT NULL, `isSynced` INTEGER NOT NULL, `budgetId` TEXT, `categoryId` TEXT, `tagIds` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("INSERT INTO `transactions_new` (id, accountId, amount, type, category, subCategory, source, description, transactionDate, merchant, bank, maskedAccountNo, provider, isSynced, budgetId, categoryId, tagIds) SELECT id, accountId, amount, type, category, subCategory, source, description, transactionDate, merchant, bank, maskedAccountNo, provider, isSynced, budgetId, categoryId, tagIds FROM `transactions`")
        db.execSQL("DROP TABLE `transactions`")
        db.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `referenceNumber` TEXT")
    }
}

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `contactName` TEXT")
    }
}

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `targetAmount` REAL NOT NULL, `savedAmount` REAL NOT NULL, `monthlySavings` REAL NOT NULL, `durationMonths` INTEGER NOT NULL, `iconResId` TEXT NOT NULL, `colorHex` TEXT NOT NULL, `creationDate` TEXT NOT NULL, `isAchieved` INTEGER NOT NULL, `lastModifiedTimeStamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `notes` TEXT")
    }
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `isBookmarked` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `isCash` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `hasReceipt` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `isExcludedFromCashFlow` INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `goals` ADD COLUMN `tagId` TEXT")
    }
}

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `goals` ADD COLUMN `imageUri` TEXT")
    }
}

val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` RENAME COLUMN `merchant` TO `receiverName`")
    }
}

val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` RENAME COLUMN `receiverName` TO `partyName`")
    }
}

val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `mode` TEXT")
    }
}

val MIGRATION_20_21 = object : Migration(20, 21) { override fun migrate(db: SupportSQLiteDatabase) {} }
val MIGRATION_21_22 = object : Migration(21, 22) { override fun migrate(db: SupportSQLiteDatabase) {} }
val MIGRATION_22_23 = object : Migration(22, 23) { override fun migrate(db: SupportSQLiteDatabase) {} }
val MIGRATION_23_24 = object : Migration(23, 24) { override fun migrate(db: SupportSQLiteDatabase) {} }

val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `budgets` ADD COLUMN `isSynced` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `goals` ADD COLUMN `isSynced` INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_25_26 = object : Migration(25, 26) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `senderName` TEXT")
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `receiverName` TEXT")
        db.execSQL("DROP TABLE IF EXISTS `notifications`")
        db.execSQL("CREATE TABLE `notifications` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `featureId` TEXT, `iconResId` TEXT, `isRead` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_26_27 = object : Migration(26, 27) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `notifications`")
        db.execSQL("CREATE TABLE `notifications` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `featureId` TEXT, `iconResId` TEXT, `isRead` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_27_28 = object : Migration(27, 28) {
    override fun migrate(db: SupportSQLiteDatabase) { }
}

val MIGRATION_28_29 = object : Migration(28, 29) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `budgets` ADD COLUMN `isRepeating` INTEGER NOT NULL DEFAULT 0")
    }
}

val appModule = module {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "koshpal_database")
        .addMigrations(
            MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, 
            MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, 
            MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, 
            MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20, MIGRATION_20_21,
            MIGRATION_21_22, MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25,
            MIGRATION_25_26, MIGRATION_26_27, MIGRATION_27_28, MIGRATION_28_29
        )
        .fallbackToDestructiveMigration(true)
        .build() }
    single { OkHttp.create() }
    single { createAndroidDataStore(androidContext()) }

    single<NotificationHelper> { AndroidNotificationHelper(androidContext()) }
    single<ReminderScheduler> { AndroidReminderScheduler(androidContext()) }
    single<SmsTransactionSyncer> { AndroidSmsTransactionSyncer(get()) }

    single<SmsFilter> { SmsFilterImpl() }
    single<TransactionValidator> { TransactionValidatorImpl() }
    single<DuplicateDetector> { DuplicateDetectorImpl() }
    single<SmsReader> { SmsReaderImpl(androidContext()) }
    single { ContactResolver(androidContext()) }
    single<List<BankIdentityParser>> {
        listOf(
            BankParser.SbiSmsParser(),
            BankParser.BankOfBarodaSmsParser(),
            BankParser.BankOfIndiaSmsParser(),
            BankParser.BankOfMaharashtraSmsParser(),
            BankParser.CanaraSmsParser(),
            BankParser.CentralBankSmsParser(),
            BankParser.IndianBankSmsParser(),
            BankParser.IndianOverseasBankSmsParser(),
            BankParser.PunjabSindBankSmsParser(),
            BankParser.PnbSmsParser(),
            BankParser.UcoBankSmsParser(),
            BankParser.UnionBankSmsParser(),

            BankParser.AxisSmsParser(),
            BankParser.BandhanSmsParser(),
            BankParser.CsbSmsParser(),
            BankParser.CityUnionSmsParser(),
            BankParser.DcbSmsParser(),
            BankParser.DhanlaxmiSmsParser(),
            BankParser.FederalBankSmsParser(),
            BankParser.HdfcSmsParser(),
            BankParser.IciciSmsParser(),
            BankParser.IndusIndSmsParser(),
            BankParser.IdfcFirstSmsParser(),
            BankParser.JkBankSmsParser(),
            BankParser.KarnatakaBankSmsParser(),
            BankParser.KarurVysyaSmsParser(),
            BankParser.KotakSmsParser(),
            BankParser.NainitalBankSmsParser(),
            BankParser.RblSmsParser(),
            BankParser.SouthIndianBankSmsParser(),
            BankParser.TamilnadMercantileSmsParser(),
            BankParser.YesBankSmsParser(),
            BankParser.IdbiSmsParser(),

            BankParser.AuSfbSmsParser(),
            BankParser.CapitalSfbSmsParser(),
            BankParser.EquitasSmsParser(),
            BankParser.EsafSfbSmsParser(),
            BankParser.SuryodaySfbSmsParser(),
            BankParser.UjjivanSfbSmsParser(),
            BankParser.UtkarshSfbSmsParser(),
            BankParser.SliceSfbSmsParser(),
            BankParser.JanaSfbSmsParser(),
            BankParser.ShivalikSfbSmsParser(),
            BankParser.UnitySfbSmsParser(),

            BankParser.AirtelPaymentsBankSmsParser(),
            BankParser.IndiaPostPaymentsBankSmsParser(),
            BankParser.FinoPaymentsBankSmsParser(),
            BankParser.PaytmPaymentsBankSmsParser(),
            BankParser.JioPaymentsBankSmsParser(),
            BankParser.NsdlPaymentsBankSmsParser()
        )
    }
    factory { 
        val resolver: ContactResolver = get()
        ProcessIncomingSmsUseCase(get(), get(), get(), get(), get(), get(), get(), get(), resolver::resolveContactName) 
    }
    factory { 
        val resolver: ContactResolver = get()
        SyncSmsTransactionsUseCase(get(), get(), resolver::resolveContactName) 
    }
    factory { TransactionUseCases(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
