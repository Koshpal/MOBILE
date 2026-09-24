import SwiftUI
import Combine
import SharedCore

@MainActor
class TransactionsViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getTransactionsViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var searchQuery: String = ""
    @Published var selectedTab: String = "All"
    @Published var filteredTransactions: [SharedCore.Transaction] = []
    @Published var isEditing: Bool = false
    @Published var isFilterVisible: Bool = false
    @Published var selectedIds: Set<String> = []
    @Published var selectAll: Bool = false
    @Published var typeFilter: String = "Both"
    @Published var showBookmarked: Bool = false
    @Published var showCash: Bool = false
    @Published var showWithNotes: Bool = false
    @Published var showWithReceipts: Bool = false
    @Published var showWithoutPayorPayee: Bool = false
    @Published var showExcludedFromCashFlow: Bool = false

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await q in self.viewModel.searchQuery {
                self.searchQuery = q
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tab in self.viewModel.selectedTab {
                self.selectedTab = tab
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await list in self.viewModel.filteredTransactions {
                self.filteredTransactions = list
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await editing in self.viewModel.isEditing {
                self.isEditing = editing.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await vis in self.viewModel.isFilterVisible {
                self.isFilterVisible = vis.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tf in self.viewModel.typeFilter {
                self.typeFilter = tf
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bm in self.viewModel.showBookmarked {
                self.showBookmarked = bm.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cash in self.viewModel.showCash {
                self.showCash = cash.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await notes in self.viewModel.showWithNotes {
                self.showWithNotes = notes.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await receipts in self.viewModel.showWithReceipts {
                self.showWithReceipts = receipts.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await payor in self.viewModel.showWithoutPayorPayee {
                self.showWithoutPayorPayee = payor.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ex in self.viewModel.showExcludedFromCashFlow {
                self.showExcludedFromCashFlow = ex.boolValue
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

    func onTabSelect(_ tab: String) {
        viewModel.onTabSelect(tab: tab)
    }

    func updateClickedTransactionId(_ id: String) {
        viewModel.updateClickedTransactionId(id: id)
    }

    func deleteTransaction(_ id: String) {
        viewModel.deleteTransaction(id: id)
    }

    func toggleFilterVisibility() {
        viewModel.updateIsFilterVisible(visible: !isFilterVisible)
    }

    func updateTypeFilter(_ type: String) {
        viewModel.updateTypeFilter(value: type)
    }

    func toggleShowBookmarked() {
        viewModel.updateShowBookmarked(value: !showBookmarked)
    }

    func toggleShowCash() {
        viewModel.updateShowCash(value: !showCash)
    }

    func toggleShowWithNotes() {
        viewModel.updateShowWithNotes(value: !showWithNotes)
    }

    func toggleShowWithReceipts() {
        viewModel.updateShowWithReceipts(value: !showWithReceipts)
    }

    func toggleShowWithoutPayorPayee() {
        viewModel.updateShowWithoutPayorPayee(value: !showWithoutPayorPayee)
    }

    func toggleShowExcludedFromCashFlow() {
        viewModel.updateShowExcludedFromCashFlow(value: !showExcludedFromCashFlow)
    }

    func resetFilters() {
        viewModel.updateTypeFilter(value: "Both")
        viewModel.updateShowBookmarked(value: false)
        viewModel.updateShowCash(value: false)
        viewModel.updateShowWithNotes(value: false)
        viewModel.updateShowWithReceipts(value: false)
        viewModel.updateShowWithoutPayorPayee(value: false)
        viewModel.updateShowExcludedFromCashFlow(value: false)
    }

    func syncSmsTransactions() {
        viewModel.syncSmsTransactions()
    }
}
