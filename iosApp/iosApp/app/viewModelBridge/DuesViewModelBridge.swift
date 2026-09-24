import SwiftUI
import Combine
import SharedCore

@MainActor
class DuesViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getDuesViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var isEditing: Bool = false
    @Published var isFilterVisible: Bool = false
    @Published var isIndividualEditing: String = ""
    @Published var selectedItemIds: Set<String> = []
    @Published var selectAll: Bool = false
    @Published var searchQuery: String = ""
    @Published var selectedTab: String = "upcoming"
    @Published var showCompletedReminders: Bool = false
    @Published var filterDate: SharedCore.Kotlinx_datetimeLocalDate? = nil

    @Published var dues: [SharedCore.Due] = []
    @Published var filteredDues: [SharedCore.Due] = []
    @Published var totalUpcomingAmount: Double = 0.0
    @Published var totalOverdueAmount: Double = 0.0
    @Published var clickedDueId: String = ""
    @Published var activeDue: SharedCore.Due? = nil
    @Published var searchSuggestions: [String] = ["Upcoming", "Overdue", "Monthly", "Rent", "Salary"]

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ed in self.viewModel.isEditing {
                self.isEditing = ed.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fv in self.viewModel.isFilterVisible {
                self.isFilterVisible = fv.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ie in self.viewModel.isIndividualEditing {
                self.isIndividualEditing = ie
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sel in self.viewModel.selectedItem {
                self.selectedItemIds = sel
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sa in self.viewModel.selectAll {
                self.selectAll = sa.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sq in self.viewModel.searchQuery {
                self.searchQuery = sq
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await st in self.viewModel.selectedTab {
                self.selectedTab = st
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await scr in self.viewModel.showCompletedReminders {
                self.showCompletedReminders = scr.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fd in self.viewModel.filterDate {
                self.filterDate = fd
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await dList in self.viewModel.dues {
                self.dues = dList
                self.updateActiveDue()
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fdList in self.viewModel.filteredDues {
                self.filteredDues = fdList
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tua in self.viewModel.totalUpcomingAmount {
                self.totalUpcomingAmount = tua.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await toa in self.viewModel.totalOverdueAmount {
                self.totalOverdueAmount = toa.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cid in self.viewModel.clickedDueId {
                self.clickedDueId = cid
                self.updateActiveDue()
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ss in self.viewModel.searchSuggestions {
                self.searchSuggestions = ss.isEmpty ? ["Upcoming", "Overdue", "Monthly", "Rent", "Salary"] : ss
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    private func updateActiveDue() {
        if !clickedDueId.isEmpty {
            self.activeDue = dues.first { $0.id == clickedDueId }
        } else {
            self.activeDue = nil
        }
    }

    // ACTIONS FORWARDED TO SHARED VIEWMODEL
    func updateSearchQuery(_ value: String) {
        viewModel.updateSearchQuery(value: value)
    }

    func updateSelectedTab(_ value: String) {
        viewModel.updateSelectedTab(value: value)
    }

    func updateClickedDueId(_ value: String) {
        viewModel.updateClickedDueId(value: value)
        updateActiveDue()
    }

    func updateIsEditing(_ editing: Bool) {
        viewModel.updateIsEditing(editing: editing)
    }

    func updateIsIndividualEditing(_ id: String) {
        viewModel.updateIsIndividualEditing(id: id)
    }

    func toggleShowCompletedReminders() {
        viewModel.toggleShowCompletedReminders()
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

    func updateIsFilterVisible(_ visible: Bool) {
        viewModel.updateIsFilterVisible(visible: visible)
    }

    func updateFilterDate(_ date: SharedCore.Kotlinx_datetimeLocalDate?) {
        viewModel.updateFilterDate(date: date)
    }

    func clearReminderForm() {
        viewModel.clearReminderForm()
    }

    func deleteDue(_ id: String) {
        viewModel.deleteDue(id: id)
    }

    func deleteDue(_ due: SharedCore.Due) {
        viewModel.deleteDue(due: due)
    }

    func excludeSelection() {
        viewModel.excludeSelection()
    }

    func resetEditingState() {
        viewModel.resetEditingState()
    }

    func toggleDueCompletion(_ id: String) {
        viewModel.toggleDueCompletion(id: id)
    }

    func prepareEditDue(_ due: SharedCore.Due) {
        viewModel.prepareEditDue(due: due)
    }
}
