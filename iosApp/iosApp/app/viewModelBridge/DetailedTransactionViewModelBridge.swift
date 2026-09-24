import SwiftUI
import Combine
import SharedCore

@MainActor
class DetailedTransactionViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getDetailedTransactionViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var transaction: SharedCore.Transaction? = nil
    @Published var totalAmount: Double = 0.0

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await txn in self.viewModel.transaction {
                self.transaction = txn
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await amt in self.viewModel.detailedHeaderAmount {
                self.totalAmount = amt.doubleValue
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func getBudgetName(_ id: String?) -> String? {
        return viewModel.getBudgetName(id: id)
    }

    func getCategoryName(budgetId: String?, categoryId: String?) -> String? {
        return viewModel.getCategoryName(budgetId: budgetId, categoryId: categoryId)
    }
}
