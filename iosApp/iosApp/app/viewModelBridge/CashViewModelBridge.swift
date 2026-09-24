import SwiftUI
import Combine
import SharedCore

@MainActor
class CashViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getCashViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var isEditing: Bool = false
    @Published var selectAll: Bool = false
    @Published var selectedIds: Set<String> = []
    @Published var isFilterVisible: Bool = false
    @Published var searchQuery: String = ""
    @Published var filterPeriod: String = "All"
    @Published var startDate: Int64? = nil
    @Published var endDate: Int64? = nil
    @Published var cashBalance: Double = 0.0
    @Published var cashTrend: [Double] = []
    @Published var trendDateRange: (String, String) = ("No data", "No data")
    @Published var filteredTransactions: [SharedCore.Transaction] = []

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await editing in self.viewModel.isEditing {
                self.isEditing = editing.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await selAll in self.viewModel.selectAll {
                self.selectAll = selAll.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await set in self.viewModel.selectedIds {
                self.selectedIds = set
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
            for await query in self.viewModel.searchQuery {
                self.searchQuery = query
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await period in self.viewModel.filterPeriod {
                self.filterPeriod = period
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await start in self.viewModel.startDate {
                self.startDate = start?.int64Value
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await end in self.viewModel.endDate {
                self.endDate = end?.int64Value
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bal in self.viewModel.cashBalance {
                self.cashBalance = bal.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await trend in self.viewModel.cashTrend {
                self.cashTrend = trend.map { $0.doubleValue }
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await pair in self.viewModel.trendDateRange {
                let startStr = pair.first as? String ?? "No data"
                let endStr = pair.second as? String ?? "No data"
                self.trendDateRange = (startStr, endStr)
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await list in self.viewModel.filteredTransactions {
                self.filteredTransactions = list
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func getTagName(_ id: String?) -> String? {
        return viewModel.getTagName(id: id)
    }

    func getCategoryName(budgetId: String?, categoryId: String?) -> String? {
        return viewModel.getCategoryName(budgetId: budgetId, categoryId: categoryId)
    }

    func onSearchQueryChange(_ query: String) {
        viewModel.onSearchQueryChange(query: query)
    }

    func onFilterPeriodChange(_ value: String) {
        viewModel.onFilterPeriodChange(value: value)
    }

    func updateIsFilterVisible(_ visible: Bool) {
        viewModel.updateIsFilterVisible(visible: visible)
    }

    func updateIsEditing(_ editing: Bool) {
        viewModel.updateIsEditing(editing: editing)
    }

    func addSelectedItem(_ id: String) {
        viewModel.addSelectedItem(id: id)
    }

    func removeSelectedItem(_ id: String) {
        viewModel.removeSelectedItem(id: id)
    }

    func updateSelectAll(_ value: Bool) {
        viewModel.updateSelectAll(value: value)
    }

    func clearSelection() {
        viewModel.clearSelection()
    }

    func deleteSelection() {
        viewModel.deleteSelection()
    }
}
