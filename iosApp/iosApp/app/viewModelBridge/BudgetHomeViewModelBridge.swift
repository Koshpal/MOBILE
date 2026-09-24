import SwiftUI
import Combine
import SharedCore

@MainActor
class BudgetHomeViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getBudgetViewModel()
    private var tasks: [Task<Void, Never>] = []

    // SHARED FEATURE STATES (Observed from Shared BudgetViewModel)
    @Published var searchQuery: String = ""
    @Published var budgetTypeIs: String = "all"
    @Published var showHistory: Bool = false
    @Published var showHidden: Bool = false
    @Published var filterPeriod: SharedCore.BudgetPeriod? = nil
    @Published var filterDate: String? = nil
    
    @Published var filteredBudgets: [Budget] = []
    @Published var filteredHistoryBudgets: [Budget] = []
    @Published var flaggedBudgets: [Budget] = []
    @Published var hiddenBudgetIds: Set<String> = []
    @Published var flaggedBudgetIds: Set<String> = []
    @Published var totalBudgetedAmount: Double = 0.0
    @Published var searchSuggestions: [String] = []

    // LOCAL PRESENTATION STATES (Purely transient SwiftUI UI state)
    @Published var isEditing: Bool = false
    @Published var isFilterVisible: Bool = false
    @Published var isIndividualEditing: String = ""
    @Published var selectedItemIds: Set<String> = []
    @Published var selectAll: Bool = false

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await budgets in self.viewModel.filteredBudgets {
                self.filteredBudgets = budgets
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await history in self.viewModel.filteredHistoryBudgets {
                self.filteredHistoryBudgets = history
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await flagged in self.viewModel.flaggedBudgets {
                self.flaggedBudgets = flagged
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await total in self.viewModel.totalBudgetedAmount {
                self.totalBudgetedAmount = total.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await hiddenSet in self.viewModel.hiddenBudgetIds {
                self.hiddenBudgetIds = hiddenSet
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await flaggedSet in self.viewModel.flaggedBudgetIds {
                self.flaggedBudgetIds = flaggedSet
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await hidden in self.viewModel.showHidden {
                self.showHidden = hidden.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await hist in self.viewModel.showHistory {
                self.showHistory = hist.boolValue
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
            for await type in self.viewModel.budgetTypeIs {
                self.budgetTypeIs = type
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
            for await suggestions in self.viewModel.searchSuggestions {
                self.searchSuggestions = suggestions
            }
        })

        // Managed Event Observation with explicit Task cancellation
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await _ in self.viewModel.events {
                // Event handling
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    // ACTIONS DELEGATED TO SHARED VIEWMODEL
    func updateSearchQuery(_ query: String) {
        viewModel.updateSearchQuery(q: query)
    }

    func updateBudgetTypeFilter(_ type: String) {
        viewModel.updateBudgetTypeIs(t: type)
        self.selectedItemIds.removeAll()
        self.selectAll = false
    }

    func toggleHistory() {
        viewModel.toggleHistory()
        self.isEditing = false
        self.isFilterVisible = false
    }

    func toggleShowHidden() {
        viewModel.toggleShowHidden()
    }

    func updateFilterPeriod(_ period: SharedCore.BudgetPeriod?) {
        viewModel.updateFilterPeriod(p: period)
    }

    func updateFilterDate(_ dateString: String?) {
        self.filterDate = dateString
        if let ds = dateString, let localDate = SharedCore.DateUtilsKt.parseIsoToLocalDate(ds) {
            viewModel.updateFilterDate(d: localDate)
        } else {
            viewModel.updateFilterDate(d: nil as SharedCore.Kotlinx_datetimeLocalDate?)
        }
    }

    func toggleEditMode() {
        isEditing.toggle()
        if isEditing {
            isFilterVisible = false
        } else {
            selectedItemIds.removeAll()
            selectAll = false
            isIndividualEditing = ""
        }
    }

    func toggleFilterMode() {
        isFilterVisible.toggle()
        if isFilterVisible {
            isEditing = false
            selectedItemIds.removeAll()
            selectAll = false
            isIndividualEditing = ""
        }
    }

    func updateIsIndividualEditing(_ id: String) {
        self.isIndividualEditing = id
    }

    func addSelectedItem(_ id: String) {
        selectedItemIds.insert(id)
    }

    func removeSelectedItem(_ id: String) {
        selectedItemIds.remove(id)
        selectAll = false
    }

    func toggleSelectAll() {
        selectAll.toggle()
        if selectAll {
            let activeList = showHistory ? filteredHistoryBudgets : filteredBudgets
            selectedItemIds = Set(activeList.map { $0.id })
        } else {
            selectedItemIds.removeAll()
        }
    }

    func toggleIndividualFlaggedState(_ id: String) {
        viewModel.toggleIndividualFlaggedState(id: id)
        isIndividualEditing = ""
    }

    func toggleIndividualHiddenState(_ id: String) {
        viewModel.toggleIndividualHiddenState(id: id)
        isIndividualEditing = ""
    }

    func deleteBudget(_ budget: Budget) {
        viewModel.deleteBudget(budget: budget)
        isIndividualEditing = ""
    }

    func deleteSelectedBudgets() {
        viewModel.deleteSelectedBudgets()
        selectedItemIds.removeAll()
        selectAll = false
        isEditing = false
    }

    func flagSelectedBudgets() {
        viewModel.toggleFlaggedState()
        selectedItemIds.removeAll()
        selectAll = false
        isEditing = false
    }

    func hideSelectedBudgets() {
        viewModel.toggleSelectionHiddenState()
        selectedItemIds.removeAll()
        selectAll = false
        isEditing = false
    }

    func removeFlaggedBudget(_ id: String) {
        viewModel.removeFlaggedBudget(id: id)
    }

    var selectedTotalAmount: Double {
        let list = showHistory ? filteredHistoryBudgets : filteredBudgets
        return list.filter { selectedItemIds.contains($0.id) }.reduce(0.0) { $0 + $1.amount }
    }

    var isAnySelectedHidden: Bool {
        selectedItemIds.contains { hiddenBudgetIds.contains($0) }
    }

    var isAnySelectedFlagged: Bool {
        selectedItemIds.contains { flaggedBudgetIds.contains($0) }
    }
}
