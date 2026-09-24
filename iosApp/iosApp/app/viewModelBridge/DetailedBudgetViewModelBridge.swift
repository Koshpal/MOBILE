import SwiftUI
import Combine
import SharedCore

@MainActor
class DetailedBudgetViewModelBridge: ObservableObject {
    let viewModel = KoinIosKt.getBudgetViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var budget: SharedCore.Budget? = nil
    @Published var totalSpent: Double = 0.0
    @Published var categorySpentMap: [String: Double] = [:]
    @Published var isEditing: Bool = false
    @Published var showHistory: Bool = false
    @Published var selectedCategories: Set<String> = []
    @Published var selectAll: Bool = false

    let budgetId: String

    init(budgetId: String) {
        self.budgetId = budgetId
        viewModel.updateClickedBudgetId(id: budgetId)
        viewModel.updateIsItemClicked(c: true)

        // Observe Budgets & History Budgets to find active budget
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await budgets in self.viewModel.budgets {
                let history = self.viewModel.historyBudgets.value
                let all = budgets + history
                if let b = all.first(where: { $0.id == self.budgetId }) {
                    self.budget = b
                    self.observeSpentForBudget(b)
                }
            }
        })

        // Observe Editing state
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await editing in self.viewModel.isEditing {
                self.isEditing = editing.boolValue
            }
        })

        // Observe Show History state
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sh in self.viewModel.showHistory {
                self.showHistory = sh.boolValue
            }
        })

        // Observe Selected Categories
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cats in self.viewModel.selectedCategories {
                self.selectedCategories = cats
            }
        })

        // Observe Select All
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await all in self.viewModel.selectAll {
                self.selectAll = all.boolValue
            }
        })
    }

    private func observeSpentForBudget(_ b: SharedCore.Budget) {
        // Observe total spent flow for this budget
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await spent in self.viewModel.getSpentAmountForBudget(b: b) {
                self.totalSpent = spent.doubleValue
            }
        })

        // Observe spent flows for each category
        for cat in b.categories {
            tasks.append(Task { [weak self] in
                guard let self = self else { return }
                for await spent in self.viewModel.getSpentAmountForCategory(c: cat) {
                    self.categorySpentMap[cat.id] = spent.doubleValue
                }
            })
        }
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func updateIsEditing(_ v: Bool) {
        viewModel.updateIsEditing(e: v)
    }

    func addSelectedCategory(_ id: String) {
        viewModel.addSelectedCategory(id: id)
    }

    func removeSelectedCategory(_ id: String) {
        viewModel.removeSelectedCategory(id: id)
    }

    func updateSelectAll(_ v: Bool) {
        viewModel.updateSelectAll(v: v)
    }

    func excludeSelection() {
        viewModel.excludeSelection()
        viewModel.updateIsEditing(e: false)
    }

    func excludeIndividualCategory(_ categoryId: String) {
        viewModel.excludeIndividualCategory(id: categoryId)
    }

    func deleteBudget(onSuccess: @escaping () -> Void) {
        if let b = budget {
            viewModel.deleteBudget(budget: b)
            onSuccess()
        }
    }

    func prepareCloneBudget() {
        viewModel.prepareCloneBudget(id: budgetId)
    }

    func resetEditingState() {
        viewModel.resetEditingState()
    }
}
