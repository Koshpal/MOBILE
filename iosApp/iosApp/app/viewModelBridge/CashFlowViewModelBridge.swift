import SwiftUI
import Combine
import SharedCore

@MainActor
class CashFlowViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getCashFlowViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var searchQuery: String = ""
    @Published var selectedMonth: YearMonth? = nil
    @Published var incomeThisMonth: Double = 0.0
    @Published var expenseThisMonth: Double = 0.0
    @Published var leftThisMonth: Double = 0.0
    @Published var investedThisMonth: Double = 0.0
    @Published var incomingTransactions: [SharedCore.Transaction] = []
    @Published var outgoingTransactions: [SharedCore.Transaction] = []
    @Published var dualLineTrendData: [CashFlowFluxDeck.CashFlowPoint] = []

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await query in self.viewModel.searchQuery {
                self.searchQuery = query
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await month in self.viewModel.selectedMonth {
                self.selectedMonth = month
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await inc in self.viewModel.incomeThisMonth {
                self.incomeThisMonth = inc.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await exp in self.viewModel.expenseThisMonth {
                self.expenseThisMonth = exp.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await left in self.viewModel.leftThisMonth {
                self.leftThisMonth = left.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await inv in self.viewModel.investedThisMonth {
                self.investedThisMonth = inv.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await list in self.viewModel.incomingTransactions {
                self.incomingTransactions = list
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await list in self.viewModel.outgoingTransactions {
                self.outgoingTransactions = list
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await trend in self.viewModel.dualLineTrendData {
                self.dualLineTrendData = trend
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func onSearchQueryChange(_ query: String) {
        viewModel.onSearchQueryChange(query: query)
    }

    func onSelectedMonthChange(_ ym: YearMonth?) {
        viewModel.onSelectedMonthChange(ym: ym)
    }

    func selectPreviousMonth() {
        viewModel.selectPreviousMonth()
    }

    func selectNextMonth() {
        viewModel.selectNextMonth()
    }

    func toggleAllTime(_ showAll: Bool) {
        viewModel.toggleAllTime(showAll: showAll)
    }
}
