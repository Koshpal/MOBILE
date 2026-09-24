package com.app.koshpal.shared.di

import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.data.repository.AuthRepoImpl
import com.app.koshpal.app.data.repository.BudgetRepoImpl
import com.app.koshpal.app.data.repository.CategoryRepoImpl
import com.app.koshpal.app.data.repository.DueRepoImpl
import com.app.koshpal.app.data.repository.GoalRepoImpl
import com.app.koshpal.app.data.repository.NotificationRepoImpl
import com.app.koshpal.app.data.repository.ReminderTypeRepoImpl
import com.app.koshpal.app.data.repository.TagRepoImpl
import com.app.koshpal.app.data.repository.TransactionsRepoImpl
import com.app.koshpal.app.domain.coordinator.AuthCoordinator
import com.app.koshpal.app.domain.coordinator.BudgetCoordinator
import com.app.koshpal.app.domain.coordinator.CashCoordinator
import com.app.koshpal.app.domain.coordinator.DuesCoordinator
import com.app.koshpal.app.domain.coordinator.GoalCoordinator
import com.app.koshpal.app.domain.coordinator.ProfileCoordinator
import com.app.koshpal.app.domain.coordinator.TagsCoordinator
import com.app.koshpal.app.domain.coordinator.TransactionsCoordinator
import com.app.koshpal.app.domain.repository.AuthRepo
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.app.domain.repository.CategoryRepo
import com.app.koshpal.app.domain.repository.DueRepo
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.app.domain.repository.NotificationRepo
import com.app.koshpal.app.domain.repository.ReminderTypeRepo
import com.app.koshpal.app.domain.repository.TagRepo
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.app.domain.usecase.authusecase.AuthUseCases
import com.app.koshpal.app.domain.usecase.authusecase.LoginUseCase
import com.app.koshpal.app.domain.usecase.authusecase.OnBoardingUseCase
import com.app.koshpal.app.domain.usecase.authusecase.OnRefreshTokenUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.ArchiveBudgetUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.BudgetUseCases
import com.app.koshpal.app.domain.usecase.budgetusecase.CreateBudgetUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.DeleteAllBudgetUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.DeleteBudgetUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.DeleteRemoteBudgetUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.GetAllBudgetsUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.GetAllBudgetsWithDetailsUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.GetArchivedBudgetsUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.GetBudgetByIdUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.GetBudgetsInRangeUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.GetBudgetsUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.GetRemoteBudgetsUseCase
import com.app.koshpal.app.domain.usecase.budgetusecase.UpdateBudgetUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.CategoryUseCases
import com.app.koshpal.app.domain.usecase.categoriesusecase.CreateCategoryUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.DeleteAllCategoriesUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.DeleteCategoryUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.DeleteOrphanedCategoriesUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.GetAllCategoriesUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.GetAllCategoriesWithSubCategoriesUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.GetCategoryByIdUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.GetMainCategoriesUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.GetSubCategoriesForParentUseCase
import com.app.koshpal.app.domain.usecase.categoriesusecase.UpdateCategoryUseCase
import com.app.koshpal.app.domain.usecase.dueusecase.DeleteDueUseCase
import com.app.koshpal.app.domain.usecase.dueusecase.DeleteDuesByIdsUseCase
import com.app.koshpal.app.domain.usecase.dueusecase.DueUseCases
import com.app.koshpal.app.domain.usecase.dueusecase.GetAllDuesUseCase
import com.app.koshpal.app.domain.usecase.dueusecase.GetDueByIdUseCase
import com.app.koshpal.app.domain.usecase.dueusecase.InsertDueUseCase
import com.app.koshpal.app.domain.usecase.dueusecase.ScheduleReminderUseCase
import com.app.koshpal.app.domain.usecase.dueusecase.UpdateDueUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.CreateGoalUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.DeleteGoalUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.DeleteGoalsByIdsUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.DeleteRemoteGoalUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.GetAllGoalsUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.GetGoalByIdUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.GetRemoteGoalsUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.GoalUseCases
import com.app.koshpal.app.domain.usecase.goalusecase.UpdateGoalUseCase
import com.app.koshpal.app.domain.usecase.notificationusecase.ClearAllNotificationsUseCase
import com.app.koshpal.app.domain.usecase.notificationusecase.DeleteOldNotificationsUseCase
import com.app.koshpal.app.domain.usecase.notificationusecase.GetAllNotificationsUseCase
import com.app.koshpal.app.domain.usecase.notificationusecase.GetNotificationsInRangeUseCase
import com.app.koshpal.app.domain.usecase.notificationusecase.InsertNotificationUseCase
import com.app.koshpal.app.domain.usecase.notificationusecase.MarkNotificationAsReadUseCase
import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import com.app.koshpal.app.domain.usecase.reminderType.DeleteReminderTypeUseCase
import com.app.koshpal.app.domain.usecase.reminderType.GetAllReminderTypesUseCase
import com.app.koshpal.app.domain.usecase.reminderType.InsertReminderTypeUseCase
import com.app.koshpal.app.domain.usecase.reminderType.ReminderTypeUseCases
import com.app.koshpal.app.domain.usecase.tagusecase.CreateTagUseCase
import com.app.koshpal.app.domain.usecase.tagusecase.DeleteTagUseCase
import com.app.koshpal.app.domain.usecase.tagusecase.DeleteTagsByIdsUseCase
import com.app.koshpal.app.domain.usecase.tagusecase.GetAllTagsUseCase
import com.app.koshpal.app.domain.usecase.tagusecase.GetTagByIdUseCase
import com.app.koshpal.app.domain.usecase.tagusecase.TagUseCases
import com.app.koshpal.app.domain.usecase.tagusecase.UpdateTagUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.CreateTransactionUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.DeleteLocalTransactionsByIdsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.DeleteLocalTransactionsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.DeleteTransactionUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetAllTransactionsInRangeUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetCategorySpentUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetRecentTransactionsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetSpentForBudgetUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetSpentForCategoryByIdUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetSubCategorySpentUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetTotalSpentUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.GetTransactionUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.TransactionQueries
import com.app.koshpal.app.domain.usecase.transactionsusecase.SaveLocalTransactionsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.UpdateLocalTransactionUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.ProcessIncomingSmsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.SyncSmsTransactionsUseCase
import com.app.koshpal.core.sms.SmsTransactionPipeline
import com.app.koshpal.core.sms.dedup.DuplicateDetector
import com.app.koshpal.core.sms.dedup.DuplicateDetectorImpl
import com.app.koshpal.core.sms.filter.SmsFilter
import com.app.koshpal.core.sms.filter.SmsFilterImpl
import com.app.koshpal.core.sms.parser.BankIdentityParser
import com.app.koshpal.core.sms.parser.TransactionSmsParser
import com.app.koshpal.core.sms.parser.bank.BankParser
import com.app.koshpal.core.sms.validate.TransactionValidator
import com.app.koshpal.core.sms.validate.TransactionValidatorImpl
import com.app.koshpal.app.fluxdeck.AuthFluxDeck
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.fluxdeck.CashFlowFluxDeck
import com.app.koshpal.app.fluxdeck.CashFluxDeck
import com.app.koshpal.app.fluxdeck.DuesFluxDeck
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.fluxdeck.HomeFluxDeck
import com.app.koshpal.app.fluxdeck.NotificationsFluxDeck
import com.app.koshpal.app.fluxdeck.ProfileFluxDeck
import com.app.koshpal.app.fluxdeck.TagsFluxDeck
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
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
import com.app.koshpal.core.data.local.AppDatabase
import com.app.koshpal.core.data.local.source.BudgetLocalDataSource
import com.app.koshpal.core.data.local.source.BudgetLocalDataSourceImpl
import com.app.koshpal.core.data.local.source.CategoryLocalDataSource
import com.app.koshpal.core.data.local.source.CategoryLocalDataSourceImpl
import com.app.koshpal.core.data.local.source.DueLocalDataSource
import com.app.koshpal.core.data.local.source.DueLocalDataSourceImpl
import com.app.koshpal.core.data.local.source.GoalLocalDataSource
import com.app.koshpal.core.data.local.source.GoalLocalDataSourceImpl
import com.app.koshpal.core.data.local.source.NotificationLocalDataSource
import com.app.koshpal.core.data.local.source.NotificationLocalDataSourceImpl
import com.app.koshpal.core.data.local.source.ReminderTypeLocalDataSource
import com.app.koshpal.core.data.local.source.ReminderTypeLocalDataSourceImpl
import com.app.koshpal.core.data.local.source.TagLocalDataSource
import com.app.koshpal.core.data.local.source.TagLocalDataSourceImpl
import com.app.koshpal.core.data.local.source.TransactionLocalDataSource
import com.app.koshpal.core.data.local.source.TransactionLocalDataSourceImpl
import com.app.koshpal.core.data.networking.HttpClientFactory
import com.app.koshpal.core.data.remote.source.AuthDataSource
import com.app.koshpal.core.data.remote.source.BudgetDataSource
import com.app.koshpal.core.data.remote.source.GoalDataSource
import com.app.koshpal.core.data.remote.source.RemoteAuthDataSource
import com.app.koshpal.core.data.remote.source.RemoteBudgetDataSource
import com.app.koshpal.core.data.remote.source.RemoteGoalDataSource
import com.app.koshpal.core.data.remote.source.RemoteTransactionsDataSource
import com.app.koshpal.core.data.remote.source.TransactionsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    single<CoroutineDispatcher>(named("DefaultDispatcher")) { Dispatchers.Default }
    single<CoroutineDispatcher>(named("IODispatcher")) { Dispatchers.Default }
    single(named("MainScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

    // DAOs
    single { get<AppDatabase>().budgetDao() }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().dueDao() }
    single { get<AppDatabase>().reminderTypeDao() }
    single { get<AppDatabase>().tagDao() }
    single { get<AppDatabase>().goalDao() }
    single { get<AppDatabase>().notificationDao() }

    // Networking
    single { HttpClientFactory.create(get()) }

    // DataSources
    singleOf(::RemoteTransactionsDataSource).bind<TransactionsDataSource>()
    singleOf(::RemoteBudgetDataSource).bind<BudgetDataSource>()
    singleOf(::RemoteGoalDataSource).bind<GoalDataSource>()
    singleOf(::RemoteAuthDataSource).bind<AuthDataSource>()
    singleOf(::TransactionLocalDataSourceImpl).bind<TransactionLocalDataSource>()
    singleOf(::BudgetLocalDataSourceImpl).bind<BudgetLocalDataSource>()
    singleOf(::CategoryLocalDataSourceImpl).bind<CategoryLocalDataSource>()
    singleOf(::DueLocalDataSourceImpl).bind<DueLocalDataSource>()
    singleOf(::ReminderTypeLocalDataSourceImpl).bind<ReminderTypeLocalDataSource>()
    singleOf(::TagLocalDataSourceImpl).bind<TagLocalDataSource>()
    singleOf(::GoalLocalDataSourceImpl).bind<GoalLocalDataSource>()
    singleOf(::NotificationLocalDataSourceImpl).bind<NotificationLocalDataSource>()

    // Repositories
    single<TransactionsRepo> { TransactionsRepoImpl(get(), get(), get()) }
    single<BudgetRepo> { BudgetRepoImpl(get(), get(), get(), get()) }
    single<CategoryRepo> { CategoryRepoImpl(get()) }
    single<DueRepo> { DueRepoImpl(get()) }
    single<ReminderTypeRepo> { ReminderTypeRepoImpl(get()) }
    single<TagRepo> { TagRepoImpl(get()) }
    single<GoalRepo> { GoalRepoImpl(get(), get(), get()) }
    single<AuthRepo> { AuthRepoImpl(get(), get()) }
    single<NotificationRepo> { NotificationRepoImpl(get()) }

    // UseCases
    factory { LoginUseCase(get()) }
    factory { OnBoardingUseCase(get()) }
    factory { OnRefreshTokenUseCase(get()) }
    factory { AuthUseCases(get(), get(), get()) }
    factory { ArchiveBudgetUseCase(get()) }
    factory { CreateBudgetUseCase(get()) }
    factory { DeleteAllBudgetUseCase(get()) }
    factory { DeleteBudgetUseCase(get()) }
    factory { GetAllBudgetsUseCase(get()) }
    factory { GetAllBudgetsWithDetailsUseCase(get()) }
    factory { GetArchivedBudgetsUseCase(get()) }
    factory { GetBudgetByIdUseCase(get()) }
    factory { GetBudgetsInRangeUseCase(get()) }
    factory { GetBudgetsUseCase(get()) }
    factory { GetRemoteBudgetsUseCase(get()) }
    factory { UpdateBudgetUseCase(get()) }
    factory { DeleteRemoteBudgetUseCase(get()) }
    factory { BudgetUseCases(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    factory { CreateCategoryUseCase(get()) }
    factory { DeleteAllCategoriesUseCase(get()) }
    factory { DeleteCategoryUseCase(get()) }
    factory { GetAllCategoriesWithSubCategoriesUseCase(get()) }
    factory { GetCategoryByIdUseCase(get()) }
    factory { GetMainCategoriesUseCase(get()) }
    factory { GetSubCategoriesForParentUseCase(get()) }
    factory { UpdateCategoryUseCase(get()) }
    factory { GetAllCategoriesUseCase(get()) }
    factory { DeleteOrphanedCategoriesUseCase(get()) }
    factory { CategoryUseCases(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    factory { DeleteDueUseCase(get()) }
    factory { GetAllDuesUseCase(get()) }
    factory { GetDueByIdUseCase(get()) }
    factory { InsertDueUseCase(get()) }
    factory { ScheduleReminderUseCase(get()) }
    factory { UpdateDueUseCase(get()) }
    factory { DeleteDuesByIdsUseCase(get()) }
    factory { DueUseCases(get(), get(), get(), get(), get(), get(), get()) }

    factory { CreateGoalUseCase(get()) }
    factory { DeleteGoalUseCase(get()) }
    factory { DeleteGoalsByIdsUseCase(get()) }
    factory { GetAllGoalsUseCase(get()) }
    factory { GetGoalByIdUseCase(get()) }
    factory { UpdateGoalUseCase(get()) }
    factory { GetRemoteGoalsUseCase(get()) }
    factory { DeleteRemoteGoalUseCase(get()) }
    factory { GoalUseCases(get(), get(), get(), get(), get(), get(), get(), get()) }

    factory { CreateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetTransactionUseCase(get()) }
    factory { GetTotalSpentUseCase(get()) }
    factory { GetCategorySpentUseCase(get()) }
    factory { GetSubCategorySpentUseCase(get()) }
    factory { GetSpentForCategoryByIdUseCase(get()) }
    factory { GetRecentTransactionsUseCase(get()) }
    factory { GetAllTransactionsInRangeUseCase(get()) }
    factory { UpdateLocalTransactionUseCase(get()) }
    factory { SaveLocalTransactionsUseCase(get()) }
    factory { DeleteLocalTransactionsUseCase(get()) }
    factory { DeleteLocalTransactionsByIdsUseCase(get()) }
    factory { GetSpentForBudgetUseCase(get()) }
    factory { TransactionQueries(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    single<SmsFilter> { SmsFilterImpl() }
    single<TransactionValidator> { TransactionValidatorImpl() }
    single<DuplicateDetector> { DuplicateDetectorImpl() }
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
    single { TransactionSmsParser(get()) }
    single { SmsTransactionPipeline(get(), get(), get(), get(), get(), get()) }

    factory { ProcessIncomingSmsUseCase(get(), get(), get(), get(), get(), get(), get(), getOrNull()) }
    factory { SyncSmsTransactionsUseCase(get(), get(), getOrNull()) }

    factory { GetAllReminderTypesUseCase(get()) }
    factory { InsertReminderTypeUseCase(get()) }
    factory { DeleteReminderTypeUseCase(get()) }
    factory { ReminderTypeUseCases(get(), get(), get()) }

    factory { GetAllNotificationsUseCase(get()) }
    factory { InsertNotificationUseCase(get()) }
    factory { DeleteOldNotificationsUseCase(get()) }
    factory { MarkNotificationAsReadUseCase(get()) }
    factory { GetNotificationsInRangeUseCase(get()) }
    factory { ClearAllNotificationsUseCase(get()) }
    factory { NotificationUseCases(get(), get(), get(), get(), get(), get()) }

    factory { GetAllTagsUseCase(get()) }
    factory { GetTagByIdUseCase(get()) }
    factory { CreateTagUseCase(get()) }
    factory { UpdateTagUseCase(get()) }
    factory { DeleteTagUseCase(get()) }
    factory { DeleteTagsByIdsUseCase(get()) }
    factory { TagUseCases(get(), get(), get(), get(), get(), get()) }

    // UserPreferences
    single { UserPreferences(get()) }

    // FluxDecks
    single { BudgetFluxDeck(get()) }
    single { DuesFluxDeck() }
    single { TagsFluxDeck(get()) }
    single { TransactionsFluxDeck() }
    single { CashFluxDeck() }
    single { GoalFluxDeck() }
    single { CashFlowFluxDeck(get(), get()) }
    single { AuthFluxDeck() }
    single { ProfileFluxDeck(get()) }
    single { NotificationsFluxDeck(get()) }
    single { HomeFluxDeck(get(), get(), get(), get(), get()) }

    // Coordinators
    single(createdAtStart = true) { BudgetCoordinator(get(), get(), get(), get(), get(), get(), get(), get(), get(named("MainScope"))) }
    single(createdAtStart = true) { GoalCoordinator(get(), get(), get(), get(), get(), get(), get(), get(named("MainScope"))) }
    single(createdAtStart = true) { TransactionsCoordinator(get(), get(), get(), get(), get(), get(), get(), get(named("MainScope"))) }
    single(createdAtStart = true) { DuesCoordinator(get(), get(), get(), get(), get(), get(), get(named("MainScope"))) }
    single(createdAtStart = true) { TagsCoordinator(get(), get(), get(), get(), get(), get(), get(), get(), get(named("MainScope"))) }
    single(createdAtStart = true) { CashCoordinator(get(), get(), get(), get(), get(named("MainScope"))) }
    single(createdAtStart = true) { ProfileCoordinator(get(), get(), { get() }, get(named("MainScope"))) }
    single(createdAtStart = true) { AuthCoordinator(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(named("IODispatcher")), get(named("MainScope"))) }

    // ViewModels
    factory { CashViewModel(get(), get()) }
    factory { HomeViewModel(get(), get(), get(), get()) }
    factory { AuthViewModel(get(), get()) }
    factory { BudgetCreationViewModel(get(), get()) }
    factory { BudgetSettingsViewModel(get(), get()) }
    factory { BudgetViewModel(get(), get()) }
    factory { CashFlowViewModel(get()) }
    factory { DuesCreationViewModel(get(), get()) }
    factory { DuesViewModel(get(), get()) }
    factory { GoalCreationViewModel(get(), get()) }
    factory { GoalViewModel(get(), get()) }
    factory { NotificationsViewModel(get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { TagsCreationViewModel(get(), get()) }
    factory { TagsViewModel(get(), get()) }
    factory { DetailedTransactionViewModel(get(), get()) }
    factory { TransactionCreationViewModel(get(), get()) }
    factory { TransactionsViewModel(get(), get()) }
}


