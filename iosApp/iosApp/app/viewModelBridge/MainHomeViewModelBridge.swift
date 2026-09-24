import SwiftUI
import Combine
import SharedCore

@MainActor
class MainHomeViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getHomeViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var firstName: String = ""
    @Published var activeMonthlyBudget: SharedCore.Budget? = nil
    @Published var monthlyBudgetContext: SharedCore.BudgetContext = SharedCore.BudgetContext(count: 0, firstId: nil, monthName: "")
    @Published var spendingSummary: SharedCore.SpendingSummary = SharedCore.SpendingSummary(outgoing: 0.0, incoming: 0.0, budgetUsed: 0.0)
    @Published var untaggedAmount: Double = 0.0
    @Published var topDues: [String: [SharedCore.DueWithMetadata]] = [:]
    @Published var recentTransactions: [SharedCore.Transaction] = []
    @Published var tagsSummary: [SharedCore.HomeTagSummary] = []
    @Published var goals: [SharedCore.Goal] = []

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fn in self.viewModel.firstName {
                self.firstName = fn
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await amb in self.viewModel.activeMonthlyBudget {
                self.activeMonthlyBudget = amb
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await mbc in self.viewModel.monthlyBudgetContext {
                self.monthlyBudgetContext = mbc
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ss in self.viewModel.spendingSummary {
                self.spendingSummary = ss
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ua in self.viewModel.untaggedAmount {
                self.untaggedAmount = ua.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tdMap in self.viewModel.topDues {
                self.topDues = tdMap
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rt in self.viewModel.recentTransactions {
                self.recentTransactions = rt.transactions
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ts in self.viewModel.tagsSummary {
                self.tagsSummary = ts
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await gList in self.viewModel.goals {
                self.goals = gList
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func toggleDueCompletion(_ metadata: SharedCore.DueWithMetadata) {
        viewModel.toggleDueCompletion(metadata: metadata)
    }

    func getTagName(id: String?) -> String? {
        viewModel.getTagName(id: id)
    }

    func getCategoryName(budgetId: String?, categoryId: String?) -> String? {
        viewModel.getCategoryName(budgetId: budgetId, categoryId: categoryId)
    }

    func onMonthSummaryClick(onNavigate: @escaping () -> Void) {
        viewModel.onMonthSummaryClick(onNavigate: onNavigate)
    }
}
