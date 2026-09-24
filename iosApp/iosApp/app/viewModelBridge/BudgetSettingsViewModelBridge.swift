import SwiftUI
import Combine
import SharedCore

@MainActor
class BudgetSettingsViewModelBridge: ObservableObject {
    let viewModel = KoinIosKt.getBudgetSettingsViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var title: String = ""
    @Published var overallAmountString: String = ""
    @Published var period: SharedCore.BudgetPeriod = SharedCore.BudgetPeriod.monthly
    @Published var startDate: String = ""
    @Published var endDate: String = ""
    @Published var budgetType: SharedCore.BudgetType = SharedCore.BudgetType.recurring
    @Published var isRepeating: Bool = false
    @Published var allocations: [CategoryAllocationUi] = []
    @Published var overAllocatedAmount: Double = 0.0

    let budgetId: String

    init(budgetId: String) {
        self.budgetId = budgetId

        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await t in self.viewModel.title {
                self.title = t
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await a in self.viewModel.overallAmountString {
                self.overallAmountString = a
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await p in self.viewModel.period {
                self.period = p
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await s in self.viewModel.startDate {
                self.startDate = s
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await e in self.viewModel.endDate {
                self.endDate = e
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bt in self.viewModel.budgetType {
                self.budgetType = bt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rep in self.viewModel.isRepeating {
                self.isRepeating = rep.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await allocs in self.viewModel.allocations {
                self.allocations = allocs
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await over in self.viewModel.overAllocatedAmount {
                self.overAllocatedAmount = over.doubleValue
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func updateTitle(_ value: String) {
        viewModel.updateTitle(value: value)
    }

    func updateOverallAmount(_ value: String) {
        viewModel.updateOverallAmount(value: value)
    }

    func updatePeriod(_ value: SharedCore.BudgetPeriod) {
        viewModel.updatePeriod(value: value)
    }

    func updateStartDate(_ value: String) {
        viewModel.updateStartDate(value: value)
    }

    func updateEndDate(_ value: String) {
        viewModel.updateEndDate(value: value)
    }

    func updateIsRepeating(_ value: Bool) {
        viewModel.updateIsRepeating(value: value)
    }

    func updateCategoryAmount(categoryId: String, amount: String) {
        viewModel.updateCategoryAmount(categoryId: categoryId, newAmountStr: amount, isManual: true)
    }

    func removeCategory(_ category: SharedCore.Category) {
        viewModel.removeCategory(category: category)
    }

    func addCategory(_ category: SharedCore.Category) {
        viewModel.addCategory(newCategory: category)
    }

    func saveBudgetChanges(onSuccess: @escaping () -> Void) {
        viewModel.saveBudgetChanges()
        onSuccess()
    }

    func deleteBudget(onSuccess: @escaping () -> Void) {
        viewModel.deleteBudget {
            onSuccess()
        }
    }
}
