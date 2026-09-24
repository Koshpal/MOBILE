import AppIntents
import SwiftUI
import SharedCore

enum IntentTransactionType: String, AppEnum {
    case expense
    case income
    case unknown

    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Transaction Type"

    static var caseDisplayRepresentations: [IntentTransactionType: DisplayRepresentation] = [
        .expense: "Expense",
        .income: "Income",
        .unknown: "Unknown"
    ]

    var toKmpType: SharedCore.TransactionType {
        switch self {
        case .expense:
            return .expense
        case .income:
            return .income
        case .unknown:
            return .unknown
        }
    }
}

struct KoshpalShortcutsProvider: AppShortcutsProvider {
    static var appShortcuts: [AppShortcut] {
        AppShortcut(
            intent: AddTransactionIntent(),
            phrases: [
                "Add a transaction in \(.applicationName)",
                "Add transaction to \(.applicationName)",
                "Log transaction in \(.applicationName)"
            ],
            shortTitle: "Add Transaction",
            systemImageName: "plus.circle"
        )
    }
}

struct BudgetEntity: AppEntity {
    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Budget"
    static var defaultQuery = BudgetEntityQuery()

    var id: String
    var title: String

    var displayRepresentation: DisplayRepresentation {
        DisplayRepresentation(title: "\(title)")
    }
}

struct BudgetEntityQuery: EntityQuery {
    func entities(for ids: [String]) async throws -> [BudgetEntity] {
        let all = try await fetchAllBudgets()
        return all.filter { ids.contains($0.id) }
    }

    func suggestedEntities() async throws -> [BudgetEntity] {
        return try await fetchAllBudgets()
    }

    private func fetchAllBudgets() async throws -> [BudgetEntity] {
        let repo = KoinIosKt.getBudgetRepo()
        for await list in repo.getAllBudgetsWithDetails() {
            return list.map { BudgetEntity(id: $0.id, title: $0.title) }
        }
        return []
    }
}

struct CategoryEntity: AppEntity {
    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Category"
    static var defaultQuery = CategoryEntityQuery()

    var id: String
    var title: String
    var budgetId: String

    var displayRepresentation: DisplayRepresentation {
        DisplayRepresentation(title: "\(title)")
    }
}

struct CategoryEntityQuery: EntityQuery {
    func entities(for ids: [String]) async throws -> [CategoryEntity] {
        let all = try await fetchAllCategories()
        return all.filter { ids.contains($0.id) }
    }

    func suggestedEntities() async throws -> [CategoryEntity] {
        return try await fetchAllCategories()
    }

    private func fetchAllCategories() async throws -> [CategoryEntity] {
        let repo = KoinIosKt.getBudgetRepo()
        var results: [CategoryEntity] = []
        for await budgets in repo.getAllBudgetsWithDetails() {
            for b in budgets {
                for alloc in b.allocations {
                    if let cat = alloc.category, cat.parentCategoryId == nil {
                        if !results.contains(where: { $0.id == cat.id }) {
                            results.append(CategoryEntity(id: cat.id, title: cat.title, budgetId: b.id))
                        }
                    }
                }
            }
            return results
        }
        return []
    }
}

struct CategoryOptionsProvider: DynamicOptionsProvider {
    @IntentParameterDependency(\AddTransactionIntent.$budget)
    var intent: IntentProjection<AddTransactionIntent>?

    func results() async throws -> [CategoryEntity] {
        guard let budget = intent?.budget else { return [] }
        let repo = KoinIosKt.getBudgetRepo()
        var results: [CategoryEntity] = []
        for await budgets in repo.getAllBudgetsWithDetails() {
            if let selectedBudget = budgets.first(where: { $0.id == budget.id }) {
                for alloc in selectedBudget.allocations {
                    if let cat = alloc.category, cat.parentCategoryId == nil {
                        if !results.contains(where: { $0.id == cat.id }) {
                            results.append(CategoryEntity(id: cat.id, title: cat.title, budgetId: budget.id))
                        }
                    }
                }
            }
            return results
        }
        return []
    }
}

struct SubCategoryEntity: AppEntity {
    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Subcategory"
    static var defaultQuery = SubCategoryEntityQuery()

    var id: String
    var title: String
    var parentCategoryId: String

    var displayRepresentation: DisplayRepresentation {
        DisplayRepresentation(title: "\(title)")
    }
}

struct SubCategoryEntityQuery: EntityQuery {
    func entities(for ids: [String]) async throws -> [SubCategoryEntity] {
        let all = try await fetchAllSubCategories()
        return all.filter { ids.contains($0.id) }
    }

    func suggestedEntities() async throws -> [SubCategoryEntity] {
        return try await fetchAllSubCategories()
    }

    private func fetchAllSubCategories() async throws -> [SubCategoryEntity] {
        let repo = KoinIosKt.getBudgetRepo()
        var results: [SubCategoryEntity] = []
        for await budgets in repo.getAllBudgetsWithDetails() {
            for b in budgets {
                for alloc in b.allocations {
                    if let cat = alloc.category, let parentId = cat.parentCategoryId {
                        if !results.contains(where: { $0.id == cat.id }) {
                            results.append(SubCategoryEntity(id: cat.id, title: cat.title, parentCategoryId: parentId))
                        }
                    }
                }
            }
            return results
        }
        return []
    }
}

struct SubCategoryOptionsProvider: DynamicOptionsProvider {
    @IntentParameterDependency(\AddTransactionIntent.$category)
    var intent: IntentProjection<AddTransactionIntent>?

    func results() async throws -> [SubCategoryEntity] {
        guard let category = intent?.category else { return [] }
        let repo = KoinIosKt.getBudgetRepo()
        var results: [SubCategoryEntity] = []
        for await budgets in repo.getAllBudgetsWithDetails() {
            for b in budgets {
                for alloc in b.allocations {
                    if let cat = alloc.category, cat.parentCategoryId == category.id {
                        if !results.contains(where: { $0.id == cat.id }) {
                            results.append(SubCategoryEntity(id: cat.id, title: cat.title, parentCategoryId: category.id))
                        }
                    }
                }
            }
            return results
        }
        return []
    }
}

struct AddTransactionIntent: AppIntent {
    static var title: LocalizedStringResource = "Add Transaction to Koshpal with Siri AI"
    static var description = IntentDescription("Saves a structured financial transaction to Koshpal.")

    static var authenticationPolicy: IntentAuthenticationPolicy = .requiresAuthentication

    static var isDiscoverable: Bool = true

    @Parameter(title: "Amount")
    var amount: Double

    @Parameter(title: "Transaction Type")
    var transactionType: IntentTransactionType

    @Parameter(title: "Categorize Transaction")
    var categorizeTransaction: Bool?

    @Parameter(title: "Budget")
    var budget: BudgetEntity?

    @Parameter(title: "Category", optionsProvider: CategoryOptionsProvider())
    var category: CategoryEntity?

    @Parameter(title: "Subcategory", optionsProvider: SubCategoryOptionsProvider())
    var subcategory: SubCategoryEntity?

    @Parameter(title: "Date")
    var date: Date?

    @Parameter(title: "Sender Name")
    var senderName: String?

    @Parameter(title: "Receiver Name")
    var receiverName: String?

    @Parameter(title: "Contact Name")
    var contactName: String?

    @Parameter(title: "Bank")
    var bank: String?

    @Parameter(title: "Masked Account Number")
    var maskedAccountNo: Int?

    @Parameter(title: "Payment Mode")
    var paymentMode: String?

    @Parameter(title: "Provider")
    var provider: String?

    @Parameter(title: "Reference Number")
    var referenceNumber: String?

    @Parameter(title: "Description")
    var transactionDescription: String?

    @Parameter(title: "Notes")
    var notes: String?

    @Parameter(title: "Is Cash")
    var isCash: Bool?

    @Parameter(title: "Is Bookmarked")
    var isBookmarked: Bool?

    @Parameter(title: "Has Receipt")
    var hasReceipt: Bool?

    @Parameter(title: "Is Excluded From Cash Flow")
    var isExcludedFromCashFlow: Bool?

    func perform() async throws -> some IntentResult & ProvidesDialog {
        let loggedInUser = await getLoggedInUser()
        guard !loggedInUser.isEmpty else {
            return .result(dialog: "Please log in to Koshpal before adding transactions with Siri.")
        }

        let isFeatureEnabled = await isSiriFeatureEnabled()
        guard isFeatureEnabled else {
            return .result(dialog: "Automatically adding transactions with Siri is disabled. You can enable it in Koshpal under Profile → Settings.")
        }

        var selectedBudget: BudgetEntity? = budget
        var selectedCategory: CategoryEntity? = category
        var selectedSubCategory: SubCategoryEntity? = subcategory

        let shouldCategorize = categorizeTransaction ?? false

        if shouldCategorize {
            let availableBudgets = try await fetchAllBudgets()
            if !availableBudgets.isEmpty {
                if selectedBudget == nil {
                    selectedBudget = try await $budget.requestValue("Which budget should I use?")
                }

                if let b = selectedBudget, selectedCategory == nil {
                    selectedCategory = try await $category.requestValue("Which category from \(b.title)?")
                }

                if let cat = selectedCategory, selectedSubCategory == nil {
                    let allBudgets = try await fetchAllBudgets()
                    let hasSubCats = allBudgets.contains { b in
                        b.allocations.contains { alloc in
                            alloc.category?.parentCategoryId == cat.id
                        }
                    }
                    if hasSubCats {
                        selectedSubCategory = try await $subcategory.requestValue("Which subcategory?")
                    }
                }
            }
        }

        let timestamp = date != nil ? Int64(date!.timeIntervalSince1970 * 1000.0) : Int64(Date().timeIntervalSince1970 * 1000.0)
        let isCashMode = isCash ?? false

        let finalBudgetId = shouldCategorize ? selectedBudget?.id : nil
        let finalCategoryId = shouldCategorize ? selectedCategory?.id : nil
        let finalCategoryTitle = shouldCategorize ? (selectedCategory?.title ?? "Uncategorized") : "Uncategorized"
        let finalSubCategoryTitle = shouldCategorize ? (selectedSubCategory?.title ?? "") : ""

        let transaction = SharedCore.Transaction(
            id: UUID().uuidString,
            accountId: "",
            amount: amount,
            type: transactionType.toKmpType,
            category: finalCategoryTitle,
            subCategory: finalSubCategoryTitle,
            source: "Siri",
            description: transactionDescription ?? "Siri Transaction",
            transactionDate: timestamp,
            senderName: senderName ?? "",
            receiverName: receiverName ?? "",
            bank: bank ?? "",
            maskedAccountNo: Int32(maskedAccountNo ?? 0),
            provider: provider ?? "Siri",
            isSynced: false,
            budgetId: finalBudgetId,
            categoryId: finalCategoryId,
            tagIds: [],
            referenceNumber: referenceNumber,
            contactName: contactName,
            notes: notes,
            isBookmarked: isBookmarked ?? false,
            isCash: isCashMode,
            hasReceipt: hasReceipt ?? false,
            isExcludedFromCashFlow: isExcludedFromCashFlow ?? false,
            mode: paymentMode
        )

        let repository = KoinIosKt.getTransactionsRepo()

        do {
            try await repository.saveLocalTransactions(transactions: SharedCore.Transactions(transactions: [transaction], page: nil, hasMore: nil))
        } catch {
            return .result(dialog: "Failed to save transaction to Koshpal: \(error.localizedDescription)")
        }

        let formattedAmount = String(format: "₹%.2f", amount)
        return .result(dialog: "Added transaction of \(formattedAmount) to Koshpal.")
    }

    private func fetchAllBudgets() async throws -> [SharedCore.Budget] {
        let repo = KoinIosKt.getBudgetRepo()
        for await list in repo.getAllBudgetsWithDetails() {
            return list
        }
        return []
    }

    private func getLoggedInUser() async -> String {
        let prefs = KoinIosKt.getUserPreferences()
        for await un in prefs.username {
            return un
        }
        return ""
    }

    private func isSiriFeatureEnabled() async -> Bool {
        let prefs = KoinIosKt.getUserPreferences()
        for await enabled in prefs.isAutoSiriTransactionsEnabled {
            return enabled.boolValue
        }
        return false
    }
}

